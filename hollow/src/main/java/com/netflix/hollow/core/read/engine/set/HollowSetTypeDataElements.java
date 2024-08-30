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
package com.netflix.hollow.core.read.engine.set;

import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElements;
import java.io.IOException;

/**
 * This class holds the data for a {@link HollowSetTypeReadState}.
 * 
 * During a delta, the HollowSetTypeReadState will create a new HollowSetTypeDataElements and atomically swap
 * with the existing one to make sure a consistent view of the data is always available. 
 */

public class HollowSetTypeDataElements extends AbstractHollowTypeDataElements {

    public FixedLengthData setPointerAndSizeData;
    public FixedLengthData elementData;

    int bitsPerSetPointer;
    int bitsPerSetSizeValue;
    int bitsPerFixedLengthSetPortion;
    int bitsPerElement;
    int emptyBucketValue;
    long totalNumberOfBuckets;

    public HollowSetTypeDataElements(ArraySegmentRecycler memoryRecycler) {
        this(MemoryMode.ON_HEAP, memoryRecycler);
    }

    public HollowSetTypeDataElements(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        super(memoryMode, memoryRecycler);
    }

    void readSnapshot(HollowBlobInput in) throws IOException {
        readFromInput(in,false);
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

        bitsPerSetPointer = VarInt.readVInt(in);
        bitsPerSetSizeValue = VarInt.readVInt(in);
        bitsPerElement = VarInt.readVInt(in);
        bitsPerFixedLengthSetPortion = bitsPerSetPointer + bitsPerSetSizeValue;
        emptyBucketValue = (1 << bitsPerElement) - 1;
        totalNumberOfBuckets = VarInt.readVLong(in);

        setPointerAndSizeData = FixedLengthDataFactory.get(in, memoryMode, memoryRecycler);
        elementData = FixedLengthDataFactory.get(in, memoryMode, memoryRecycler);
    }

    static void discardFromStream(HollowBlobInput in, int numShards, boolean isDelta) throws IOException {
        if(numShards > 1)
            VarInt.readVInt(in); // max ordinal

        for(int i=0;i<numShards;i++) {
            VarInt.readVInt(in); // max ordinal

            if(isDelta) {
                /// addition/removal ordinals
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
            }

            /// statistics
            VarInt.readVInt(in);
            VarInt.readVInt(in);
            VarInt.readVInt(in);
            VarInt.readVLong(in);

            /// fixed-length data
            FixedLengthData.discardFrom(in);
            FixedLengthData.discardFrom(in);
        }
    }

    public void applyDelta(HollowSetTypeDataElements fromData, HollowSetTypeDataElements deltaData) {
        new HollowSetDeltaApplicator(fromData, deltaData, this).applyDelta();
    }

    public void destroy() {
        FixedLengthDataFactory.destroy(setPointerAndSizeData, memoryRecycler);
        FixedLengthDataFactory.destroy(elementData, memoryRecycler);
    }

    public long getStartBucket(int ordinal) {
        return ordinal == 0 ? 0 : setPointerAndSizeData.getElementValue((long)(ordinal - 1) * bitsPerFixedLengthSetPortion, bitsPerSetPointer);
    }

    public long getEndBucket(int ordinal) {
        return setPointerAndSizeData.getElementValue((long) ordinal * bitsPerFixedLengthSetPortion, bitsPerSetPointer);
    }

    public int getBucketValue(long absoluteBucketIndex) {
        return (int)elementData.getElementValue(absoluteBucketIndex * bitsPerElement, bitsPerElement);
    }

    public void copyBucketsFrom(long startBucket, HollowSetTypeDataElements src, long srcStartBucket, long srcEndBucket) {
        if (bitsPerElement == src.bitsPerElement) {
            // fast path can bulk copy buckets. emptyBucketValue is same since bitsPerElement is same
            long numBuckets = srcEndBucket - srcStartBucket;
            elementData.copyBits(src.elementData, srcStartBucket * bitsPerElement, startBucket * bitsPerElement, numBuckets * bitsPerElement);
        } else {
            for (long bucket=srcStartBucket;bucket<srcEndBucket;bucket++) {
                long bucketVal = src.elementData.getElementValue(bucket * src.bitsPerElement, src.bitsPerElement);
                if(bucketVal == src.emptyBucketValue)
                    bucketVal = emptyBucketValue;
                elementData.setElementValue(startBucket * bitsPerElement, bitsPerElement, bucketVal);
                startBucket++;
            }
        }
    }
}
