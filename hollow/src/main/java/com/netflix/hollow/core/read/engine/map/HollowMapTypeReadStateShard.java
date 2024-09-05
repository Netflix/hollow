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

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.HollowTypeReadStateShard;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.BitSet;

class HollowMapTypeReadStateShard implements HollowTypeReadStateShard {

    final HollowMapTypeDataElements dataElements;
    final int shardOrdinalShift;

    @Override
    public HollowMapTypeDataElements getDataElements() {
        return dataElements;
    }

    @Override
    public int getShardOrdinalShift() {
        return shardOrdinalShift;
    }

    public HollowMapTypeReadStateShard(HollowMapTypeDataElements dataElements, int shardOrdinalShift) {
        this.shardOrdinalShift = shardOrdinalShift;
        this.dataElements = dataElements;
    }

    public int size(int ordinal) {
        int size = (int)dataElements.mapPointerAndSizeData.getElementValue(((long)ordinal * dataElements.bitsPerFixedLengthMapPortion) + dataElements.bitsPerMapPointer, dataElements.bitsPerMapSizeValue);
        return size;
    }


    public long relativeBucket(long absoluteBucketIndex) {
        long bucketValue;
        long key = dataElements.getBucketKeyByAbsoluteIndex(absoluteBucketIndex);
        if(key == dataElements.emptyBucketKeyValue)
            return -1L;
        bucketValue = key << 32 | dataElements.getBucketValueByAbsoluteIndex(absoluteBucketIndex);
        return bucketValue;
    }

    protected void applyShardToChecksum(HollowChecksum checksum, BitSet populatedOrdinals, int shardNumber, int numShards) {
        int ordinal = populatedOrdinals.nextSetBit(shardNumber);
        while(ordinal != ORDINAL_NONE) {
            if((ordinal & (numShards - 1)) == shardNumber) {
                int shardOrdinal = ordinal / numShards;
                int numBuckets = HashCodes.hashTableSize(size(shardOrdinal));
                long offset = dataElements.getStartBucket(shardOrdinal);

                checksum.applyInt(ordinal);
                for(int i=0; i<numBuckets; i++) {
                    int bucketKey = dataElements.getBucketKeyByAbsoluteIndex(offset + i);
                    if(bucketKey != dataElements.emptyBucketKeyValue) {
                        checksum.applyInt(i);
                        checksum.applyInt(bucketKey);
                        checksum.applyInt(dataElements.getBucketValueByAbsoluteIndex(offset + i));
                    }
                }
                ordinal = ordinal + numShards;
            } else {
                // Round up ordinal
                int r = (ordinal & -numShards) + shardNumber;
                ordinal = (r <= ordinal) ? r + numShards : r;
            }
            ordinal = populatedOrdinals.nextSetBit(ordinal);
        }
    }

    public long getApproximateHeapFootprintInBytes() {
        long requiredBitsForMapPointers = ((long)dataElements.maxOrdinal + 1) * dataElements.bitsPerFixedLengthMapPortion;
        long requiredBitsForMapBuckets = (long)dataElements.totalNumberOfBuckets * dataElements.bitsPerMapEntry;
        long requiredBits = requiredBitsForMapPointers + requiredBitsForMapBuckets;
        return requiredBits / 8;
    }
    
    public long getApproximateHoleCostInBytes(BitSet populatedOrdinals, int shardNumber, int numShards) {
        long holeBits = 0;
        
        int holeOrdinal = populatedOrdinals.nextClearBit(0);
        while(holeOrdinal <= dataElements.maxOrdinal) {
            if((holeOrdinal & (numShards - 1)) == shardNumber)
                holeBits += dataElements.bitsPerFixedLengthMapPortion;
            
            holeOrdinal = populatedOrdinals.nextClearBit(holeOrdinal + 1);
        }
        
        return holeBits / 8;
    }
}
