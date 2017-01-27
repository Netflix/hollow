package com.netflix.hollow.core.read.engine.set;

import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.SetMapKeyHasher;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.BitSet;

class HollowSetTypeReadStateShard {

    private HollowSetTypeDataElements currentData;
    private volatile HollowSetTypeDataElements currentDataVolatile;

    private HollowPrimaryKeyValueDeriver keyDeriver;

    public int size(int ordinal) {
        HollowSetTypeDataElements currentData;
        int size;

        do {
            currentData = this.currentData;
            size = (int)currentData.setPointerAndSizeArray.getElementValue((long)(ordinal * currentData.bitsPerFixedLengthSetPortion) + currentData.bitsPerSetPointer, currentData.bitsPerSetSizeValue);
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
                currentData = this.currentData;

                startBucket = getAbsoluteBucketStart(currentData, ordinal);
                endBucket = currentData.setPointerAndSizeArray.getElementValue((long)ordinal * currentData.bitsPerFixedLengthSetPortion, currentData.bitsPerSetPointer);
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
                currentData = this.currentData;

                startBucket = getAbsoluteBucketStart(currentData, ordinal);
                endBucket = currentData.setPointerAndSizeArray.getElementValue((long)ordinal * currentData.bitsPerFixedLengthSetPortion, currentData.bitsPerSetPointer);
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

        return -1;
    }
    

    public int relativeBucketValue(int setOrdinal, int bucketIndex) {
        HollowSetTypeDataElements currentData;
        int value;

        do {
            long startBucket;
            do {
                currentData = this.currentData;

                startBucket = getAbsoluteBucketStart(currentData, setOrdinal);
            } while(readWasUnsafe(currentData));

            value = absoluteBucketValue(currentData, startBucket + bucketIndex);

            if(value == currentData.emptyBucketValue)
                value = -1;
        } while(readWasUnsafe(currentData));

        return value;
    }

    private long getAbsoluteBucketStart(HollowSetTypeDataElements currentData, int ordinal) {
        return ordinal == 0 ? 0 : currentData.setPointerAndSizeArray.getElementValue((long)(ordinal - 1) * currentData.bitsPerFixedLengthSetPortion, currentData.bitsPerSetPointer);
    }

    private int absoluteBucketValue(HollowSetTypeDataElements currentData, long absoluteBucketIndex) {
        return (int)currentData.elementArray.getElementValue(absoluteBucketIndex * currentData.bitsPerElement, currentData.bitsPerElement);
    }
    
    void invalidate() {
        setCurrentData(null);
    }

    HollowSetTypeDataElements currentDataElements() {
        return currentData;
    }

    private boolean readWasUnsafe(HollowSetTypeDataElements data) {
        return data != currentDataVolatile;
    }

    void setCurrentData(HollowSetTypeDataElements data) {
        this.currentData = data;
        this.currentDataVolatile = data;
    }

    protected void applyToChecksum(HollowChecksum checksum, BitSet populatedOrdinals, int shardNumber, int numShards) {
        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
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
            }

            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
    }

    public long getApproximateHeapFootprintInBytes() {
        long requiredBitsForSetPointers = (long)currentData.bitsPerFixedLengthSetPortion * (currentData.maxOrdinal + 1);
        long requiredBitsForBuckets = (long)currentData.bitsPerElement * currentData.totalNumberOfBuckets;
        long requiredBits = requiredBitsForSetPointers + requiredBitsForBuckets;
        return requiredBits / 8;
    }
    
    public long getApproximateHoleCostInBytes(BitSet populatedOrdinals, int shardNumber, int numShards) {
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
