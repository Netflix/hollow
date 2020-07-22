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

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.SetMapKeyHasher;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.BitSet;

class HollowSetTypeReadStateShard {

    private volatile HollowSetTypeDataElements currentDataVolatile;

    private HollowPrimaryKeyValueDeriver keyDeriver;

    public int size(int ordinal) {
        HollowSetTypeDataElements currentData;
        int size;

        do {
            currentData = this.currentDataVolatile;
            size = (int)currentData.setPointerAndSizeData.getElementValue(((long)ordinal * currentData.bitsPerFixedLengthSetPortion) + currentData.bitsPerSetPointer, currentData.bitsPerSetSizeValue);
        } while(readWasUnsafe(currentData));

        return size;
    }

    public boolean contains(int ordinal, int value, int hashCode) {
        HollowSetTypeDataElements currentData;
        boolean foundData;

        threadsafe:
        do {
            long startBucket;
            long endBucket;

            do {
                currentData = this.currentDataVolatile;

                startBucket = getAbsoluteBucketStart(currentData, ordinal);
                endBucket = currentData.setPointerAndSizeData.getElementValue((long)ordinal * currentData.bitsPerFixedLengthSetPortion, currentData.bitsPerSetPointer);
            } while(readWasUnsafe(currentData));

            hashCode = HashCodes.hashInt(hashCode);
            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketOrdinal = absoluteBucketValue(currentData, bucket);

            while(bucketOrdinal != currentData.emptyBucketValue) {
                if(bucketOrdinal == value) {
                    foundData = true;
                    continue threadsafe;
                }
                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketOrdinal = absoluteBucketValue(currentData, bucket);
            }

            foundData = false;
        } while(readWasUnsafe(currentData));

        return foundData;
    }
    
    public int findElement(int ordinal, Object... hashKey) {
        int hashCode = SetMapKeyHasher.hash(hashKey, keyDeriver.getFieldTypes());

        HollowSetTypeDataElements currentData;

        threadsafe:
        do {
            long startBucket;
            long endBucket;

            do {
                currentData = this.currentDataVolatile;

                startBucket = getAbsoluteBucketStart(currentData, ordinal);
                endBucket = currentData.setPointerAndSizeData.getElementValue((long)ordinal * currentData.bitsPerFixedLengthSetPortion, currentData.bitsPerSetPointer);
            } while(readWasUnsafe(currentData));

            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketOrdinal = absoluteBucketValue(currentData, bucket);

            while(bucketOrdinal != currentData.emptyBucketValue) {
                if(readWasUnsafe(currentData))
                    continue threadsafe;
                
                if(keyDeriver.keyMatches(bucketOrdinal, hashKey))
                    return bucketOrdinal;
                
                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketOrdinal = absoluteBucketValue(currentData, bucket);
            }

        } while(readWasUnsafe(currentData));

        return ORDINAL_NONE;
    }

    public int relativeBucketValue(int setOrdinal, int bucketIndex) {
        HollowSetTypeDataElements currentData;
        int value;

        do {
            long startBucket;
            do {
                currentData = this.currentDataVolatile;

                startBucket = getAbsoluteBucketStart(currentData, setOrdinal);
            } while(readWasUnsafe(currentData));

            value = absoluteBucketValue(currentData, startBucket + bucketIndex);

            if(value == currentData.emptyBucketValue)
                value = ORDINAL_NONE;
        } while(readWasUnsafe(currentData));

        return value;
    }

    private long getAbsoluteBucketStart(HollowSetTypeDataElements currentData, int ordinal) {
        return ordinal == 0 ? 0 : currentData.setPointerAndSizeData.getElementValue((long)(ordinal - 1) * currentData.bitsPerFixedLengthSetPortion, currentData.bitsPerSetPointer);
    }

    private int absoluteBucketValue(HollowSetTypeDataElements currentData, long absoluteBucketIndex) {
        return (int)currentData.elementData.getElementValue(absoluteBucketIndex * currentData.bitsPerElement, currentData.bitsPerElement);
    }
    
    void invalidate() {
        setCurrentData(null);
    }

    HollowSetTypeDataElements currentDataElements() {
        return currentDataVolatile;
    }

    private boolean readWasUnsafe(HollowSetTypeDataElements data) {
        HollowUnsafeHandle.getUnsafe().loadFence();
        return data != currentDataVolatile;
    }

    void setCurrentData(HollowSetTypeDataElements data) {
        this.currentDataVolatile = data;
    }

    protected void applyToChecksum(HollowChecksum checksum, BitSet populatedOrdinals, int shardNumber, int numShards) {
        HollowSetTypeDataElements currentData = currentDataVolatile;
        int ordinal = populatedOrdinals.nextSetBit(shardNumber);
        while(ordinal != ORDINAL_NONE) {
            if((ordinal & (numShards - 1)) == shardNumber) {
                int shardOrdinal = ordinal / numShards;
                int numBuckets = HashCodes.hashTableSize(size(shardOrdinal));
                long offset = getAbsoluteBucketStart(currentData, shardOrdinal);
    
                checksum.applyInt(ordinal);
                for(int i=0;i<numBuckets;i++) {
                    int bucketValue = absoluteBucketValue(currentData, offset + i);
                    if(bucketValue != currentData.emptyBucketValue) {
                        checksum.applyInt(i);
                        checksum.applyInt(bucketValue);
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
        HollowSetTypeDataElements currentData = currentDataVolatile;
        long requiredBitsForSetPointers = ((long)currentData.maxOrdinal + 1) * currentData.bitsPerFixedLengthSetPortion;
        long requiredBitsForBuckets = currentData.totalNumberOfBuckets * currentData.bitsPerElement;
        long requiredBits = requiredBitsForSetPointers + requiredBitsForBuckets;
        return requiredBits / 8;
    }
    
    public long getApproximateHoleCostInBytes(BitSet populatedOrdinals, int shardNumber, int numShards) {
        HollowSetTypeDataElements currentData = currentDataVolatile;
        long holeBits = 0;
        
        int holeOrdinal = populatedOrdinals.nextClearBit(0);
        while(holeOrdinal <= currentData.maxOrdinal) {
            if((holeOrdinal & (numShards - 1)) == shardNumber)
                holeBits += currentData.bitsPerFixedLengthSetPortion;
            
            holeOrdinal = populatedOrdinals.nextClearBit(holeOrdinal + 1);
        }
        
        return holeBits / 8;
    }
    
    public void setKeyDeriver(HollowPrimaryKeyValueDeriver keyDeriver) {
        this.keyDeriver = keyDeriver;
    }
}
