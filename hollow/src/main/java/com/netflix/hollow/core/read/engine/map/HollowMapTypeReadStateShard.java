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
import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.SetMapKeyHasher;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.BitSet;

class HollowMapTypeReadStateShard {
    
    private volatile HollowMapTypeDataElements currentDataVolatile;

    private HollowPrimaryKeyValueDeriver keyDeriver;

    public int size(int ordinal) {
        HollowMapTypeDataElements currentData;
        int size;

        do {
            currentData = this.currentDataVolatile;
            size = (int)currentData.mapPointerAndSizeData.getElementValue(((long)ordinal * currentData.bitsPerFixedLengthMapPortion) + currentData.bitsPerMapPointer, currentData.bitsPerMapSizeValue);
        } while(readWasUnsafe(currentData));

        return size;
    }

    public int get(int ordinal, int keyOrdinal, int hashCode) {
        HollowMapTypeDataElements currentData;
        int valueOrdinal;

        threadsafe:
        do {
            long startBucket;
            long endBucket;
            do {
                currentData = this.currentDataVolatile;

                startBucket = ordinal == 0 ? 0 : currentData.mapPointerAndSizeData.getElementValue((long)(ordinal - 1) * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
                endBucket = currentData.mapPointerAndSizeData.getElementValue((long)ordinal * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
            } while(readWasUnsafe(currentData));

            hashCode = HashCodes.hashInt(hashCode);
            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);

            while(bucketKeyOrdinal != currentData.emptyBucketKeyValue) {
                if(bucketKeyOrdinal == keyOrdinal) {
                    valueOrdinal = getBucketValueByAbsoluteIndex(currentData, bucket);
                    continue threadsafe;
                }
                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);
            }

            valueOrdinal = ORDINAL_NONE;
        } while(readWasUnsafe(currentData));

        return valueOrdinal;
    }

    public int findKey(int ordinal, Object... hashKey) {
        int hashCode = SetMapKeyHasher.hash(hashKey, keyDeriver.getFieldTypes());

        HollowMapTypeDataElements currentData;

        threadsafe:
        do {
            long startBucket;
            long endBucket;
            do {
                currentData = this.currentDataVolatile;

                startBucket = ordinal == 0 ? 0 : currentData.mapPointerAndSizeData.getElementValue((long)(ordinal - 1) * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
                endBucket = currentData.mapPointerAndSizeData.getElementValue((long)ordinal * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
            } while(readWasUnsafe(currentData));

            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);

            while(bucketKeyOrdinal != currentData.emptyBucketKeyValue) {
                if(readWasUnsafe(currentData))
                    continue threadsafe;

                if(keyDeriver.keyMatches(bucketKeyOrdinal, hashKey)) {
                    return bucketKeyOrdinal;
                }

                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);
            }

        } while(readWasUnsafe(currentData));

        return ORDINAL_NONE;
    }

    public long findEntry(int ordinal, Object... hashKey) {
        int hashCode = SetMapKeyHasher.hash(hashKey, keyDeriver.getFieldTypes());

        HollowMapTypeDataElements currentData;

        threadsafe:
        do {
            long startBucket;
            long endBucket;
            do {
                currentData = this.currentDataVolatile;

                startBucket = ordinal == 0 ? 0 : currentData.mapPointerAndSizeData.getElementValue((long)(ordinal - 1) * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
                endBucket = currentData.mapPointerAndSizeData.getElementValue((long)ordinal * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
            } while(readWasUnsafe(currentData));

            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);

            while(bucketKeyOrdinal != currentData.emptyBucketKeyValue) {
                if(readWasUnsafe(currentData))
                    continue threadsafe;

                if(keyDeriver.keyMatches(bucketKeyOrdinal, hashKey)) {
                    long valueOrdinal = getBucketValueByAbsoluteIndex(currentData, bucket);
                    if(readWasUnsafe(currentData))
                        continue threadsafe;

                    return (long)bucketKeyOrdinal << 32 | valueOrdinal;
                }

                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);
            }

        } while(readWasUnsafe(currentData));

        return -1L;
    }

    public long relativeBucket(int ordinal, int bucketIndex) {
        HollowMapTypeDataElements currentData;
        long bucketValue;
        do {
            long absoluteBucketIndex;
            do {
                currentData = this.currentDataVolatile;
                absoluteBucketIndex = getAbsoluteBucketStart(currentData, ordinal) + bucketIndex;
            } while(readWasUnsafe(currentData));
            long key = getBucketKeyByAbsoluteIndex(currentData, absoluteBucketIndex);
            if(key == currentData.emptyBucketKeyValue)
                return -1L;

            bucketValue = key << 32 | getBucketValueByAbsoluteIndex(currentData, absoluteBucketIndex);
        } while(readWasUnsafe(currentData));

        return bucketValue;
    }

    private long getAbsoluteBucketStart(HollowMapTypeDataElements currentData, int ordinal) {
        long startBucket = ordinal == 0 ? 0 : currentData.mapPointerAndSizeData.getElementValue((long)(ordinal - 1) * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
        return startBucket;
    }

    private int getBucketKeyByAbsoluteIndex(HollowMapTypeDataElements currentData, long absoluteBucketIndex) {
        return (int)currentData.entryData.getElementValue(absoluteBucketIndex * currentData.bitsPerMapEntry, currentData.bitsPerKeyElement);
    }

    private int getBucketValueByAbsoluteIndex(HollowMapTypeDataElements currentData, long absoluteBucketIndex) {
        return (int)currentData.entryData.getElementValue((absoluteBucketIndex * currentData.bitsPerMapEntry) + currentData.bitsPerKeyElement, currentData.bitsPerValueElement);
    }

    void invalidate() {
        setCurrentData(null);
    }

    HollowMapTypeDataElements currentDataElements() {
        return currentDataVolatile;
    }

    private boolean readWasUnsafe(HollowMapTypeDataElements data) {
        HollowUnsafeHandle.getUnsafe().loadFence();
        return data != currentDataVolatile;
    }

    void setCurrentData(HollowMapTypeDataElements data) {
        this.currentDataVolatile = data;
    }

    protected void applyToChecksum(HollowChecksum checksum, BitSet populatedOrdinals, int shardNumber, int numShards) {
        HollowMapTypeDataElements currentData = currentDataVolatile;
        int ordinal = populatedOrdinals.nextSetBit(shardNumber);
        while(ordinal != ORDINAL_NONE) {
            if((ordinal & (numShards - 1)) == shardNumber) {
                int shardOrdinal = ordinal / numShards;
                int numBuckets = HashCodes.hashTableSize(size(shardOrdinal));
                long offset = getAbsoluteBucketStart(currentData, shardOrdinal);

                checksum.applyInt(ordinal);
                for(int i=0; i<numBuckets; i++) {
                    int bucketKey = getBucketKeyByAbsoluteIndex(currentData, offset + i);
                    if(bucketKey != currentData.emptyBucketKeyValue) {
                        checksum.applyInt(i);
                        checksum.applyInt(bucketKey);
                        checksum.applyInt(getBucketValueByAbsoluteIndex(currentData, offset + i));
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
        HollowMapTypeDataElements currentData = currentDataVolatile;
        long requiredBitsForMapPointers = ((long)currentData.maxOrdinal + 1) * currentData.bitsPerFixedLengthMapPortion;
        long requiredBitsForMapBuckets = (long)currentData.totalNumberOfBuckets * currentData.bitsPerMapEntry;
        long requiredBits = requiredBitsForMapPointers + requiredBitsForMapBuckets;
        return requiredBits / 8;
    }
    
    public long getApproximateHoleCostInBytes(BitSet populatedOrdinals, int shardNumber, int numShards) {
        HollowMapTypeDataElements currentData = currentDataVolatile;
        long holeBits = 0;
        
        int holeOrdinal = populatedOrdinals.nextClearBit(0);
        while(holeOrdinal <= currentData.maxOrdinal) {
            if((holeOrdinal & (numShards - 1)) == shardNumber)
                holeBits += currentData.bitsPerFixedLengthMapPortion;
            
            holeOrdinal = populatedOrdinals.nextClearBit(holeOrdinal + 1);
        }
        
        return holeBits / 8;
    }

    
    public void setKeyDeriver(HollowPrimaryKeyValueDeriver keyDeriver) {
        this.keyDeriver = keyDeriver;
    }

    
}
