/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.read.engine.map;

import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowTypeDataElements;
import java.io.IOException;

/**
 * This class holds the data for a {@link HollowMapTypeReadState}.
 * 
 * During a delta, the HollowMapTypeReadState will create a new HollowMapTypeDataElements and atomically swap
 * with the existing one to make sure a consistent view of the data is always available. 
 */
public class HollowMapTypeDataElements extends HollowTypeDataElements {

    FixedLengthData mapPointerAndSizeData;
    FixedLengthData entryData;

    int bitsPerMapPointer;
    int bitsPerMapSizeValue;
    int bitsPerFixedLengthMapPortion;
    int bitsPerKeyElement;
    int bitsPerValueElement;
    int bitsPerMapEntry;
    int emptyBucketKeyValue;
    long totalNumberOfBuckets;

    public HollowMapTypeDataElements(ArraySegmentRecycler memoryRecycler) {
        this(MemoryMode.ON_HEAP, memoryRecycler);
    }

    public HollowMapTypeDataElements(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        super(memoryMode, memoryRecycler);
    }

    void readSnapshot(HollowBlobInput in) throws IOException {
        readFromInput(in, false);
    }

    void readDelta(HollowBlobInput in) throws IOException {
        readFromInput(in,true);
    }

    private void readFromInput(HollowBlobInput in, boolean isDelta) throws IOException {
        maxOrdinal = VarInt.readVInt(in);

        if(isDelta) {
            encodedRemovals = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(in, memoryRecycler);
            encodedAdditions = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(in, memoryRecycler);
        }

        bitsPerMapPointer = VarInt.readVInt(in);
        bitsPerMapSizeValue = VarInt.readVInt(in);
        bitsPerKeyElement = VarInt.readVInt(in);
        bitsPerValueElement = VarInt.readVInt(in);
        bitsPerFixedLengthMapPortion = bitsPerMapPointer + bitsPerMapSizeValue;
        bitsPerMapEntry = bitsPerKeyElement + bitsPerValueElement;
        emptyBucketKeyValue = (1 << bitsPerKeyElement) - 1;
        totalNumberOfBuckets = VarInt.readVLong(in);

        mapPointerAndSizeData = FixedLengthDataFactory.get(in, memoryMode, memoryRecycler);
        entryData = FixedLengthDataFactory.get(in, memoryMode, memoryRecycler);
    }

    static void discardFromInput(HollowBlobInput in, int numShards, boolean isDelta) throws IOException {
        if(numShards > 1)
            VarInt.readVInt(in); /// max ordinal

        for(int i=0; i<numShards; i++) {
            VarInt.readVInt(in); /// max ordinal

            if(isDelta) {
                /// addition/removal ordinals
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
            }
    
            /// statistics
            VarInt.readVInt(in);
            VarInt.readVInt(in);
            VarInt.readVInt(in);
            VarInt.readVInt(in);
            VarInt.readVLong(in);
    
            /// fixed length data
            FixedLengthData.discardFrom(in);
            FixedLengthData.discardFrom(in);
        }
    }

    public void applyDelta(HollowMapTypeDataElements fromData, HollowMapTypeDataElements deltaData) {
        new HollowMapDeltaApplicator(fromData, deltaData, this).applyDelta();
    }

    @Override
    public void destroy() {
        FixedLengthDataFactory.destroy(mapPointerAndSizeData, memoryRecycler);
        FixedLengthDataFactory.destroy(entryData, memoryRecycler);
    }

    long getStartBucket(int ordinal) {
        return ordinal == 0 ? 0 : mapPointerAndSizeData.getElementValue((long)(ordinal - 1) * bitsPerFixedLengthMapPortion, bitsPerMapPointer);
    }

    long getEndBucket(int ordinal) {
        return mapPointerAndSizeData.getElementValue((long)ordinal * bitsPerFixedLengthMapPortion, bitsPerMapPointer);
    }

    int getBucketKeyByAbsoluteIndex(long absoluteBucketIndex) {
        return (int)entryData.getElementValue(absoluteBucketIndex * bitsPerMapEntry, bitsPerKeyElement);
    }

    int getBucketValueByAbsoluteIndex(long absoluteBucketIndex) {
        return (int)entryData.getElementValue((absoluteBucketIndex * bitsPerMapEntry) + bitsPerKeyElement, bitsPerValueElement);
    }

    void copyBucketsFrom(long startBucket, HollowMapTypeDataElements src, long srcStartBucket, long srcEndBucket) {
        if (bitsPerKeyElement == src.bitsPerKeyElement && bitsPerValueElement == src.bitsPerValueElement) {
            // fast path can bulk copy buckets. emptyBucketKeyValue is same since bitsPerKeyElement is the same
            long numBuckets = srcEndBucket - srcStartBucket;
            entryData.copyBits(src.entryData, srcStartBucket * bitsPerMapEntry, startBucket * bitsPerMapEntry, numBuckets * bitsPerMapEntry);
        } else {
            for (long bucket=srcStartBucket;bucket<srcEndBucket;bucket++) {
                long bucketKey = src.entryData.getElementValue(bucket * src.bitsPerMapEntry, src.bitsPerKeyElement);
                long bucketValue = src.entryData.getElementValue(bucket * src.bitsPerMapEntry + src.bitsPerKeyElement, src.bitsPerValueElement);
                if(bucketKey == src.emptyBucketKeyValue)
                    bucketKey = emptyBucketKeyValue;
                long targetBucketOffset = startBucket * bitsPerMapEntry;
                entryData.setElementValue(targetBucketOffset, bitsPerKeyElement, bucketKey);
                entryData.setElementValue(targetBucketOffset + bitsPerKeyElement, bitsPerValueElement, bucketValue);
                startBucket++;
            }
        }
    }
}
