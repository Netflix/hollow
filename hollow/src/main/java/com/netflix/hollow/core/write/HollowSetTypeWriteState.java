/*
 *  Copyright 2016-2020 Netflix, Inc.
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
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.io.DataOutputStream;
import java.io.IOException;

public class HollowSetTypeWriteState extends HollowTypeWriteState {

    /// statistics required for writing fixed length set data
    private int bitsPerSetPointer;
    private int bitsPerElement;
    private int bitsPerSetSizeValue;
    private long totalOfSetBuckets[];

    /// data required for writing snapshot or delta
    private int maxOrdinal;
    private int maxShardOrdinal[];
    private FixedLengthElementArray setPointersAndSizesArray[];
    private FixedLengthElementArray elementArray[];

    /// additional data required for writing delta
    private int numSetsInDelta[];
    private long numBucketsInDelta[];
    private ByteDataBuffer deltaAddedOrdinals[];
    private ByteDataBuffer deltaRemovedOrdinals[];

    public HollowSetTypeWriteState(HollowSetSchema schema) {
        this(schema, -1);
    }
    
    public HollowSetTypeWriteState(HollowSetSchema schema, int numShards) {
        super(schema, numShards);
    }

    @Override
    public HollowSetSchema getSchema() {
        return (HollowSetSchema)schema;
    }

    public void prepareForWrite() {
        super.prepareForWrite();

        gatherStatistics();
    }

    private void gatherStatistics() {
        if(numShards == -1)
            calculateNumShards();
        
        int maxElementOrdinal = 0;

        int maxOrdinal = ordinalMap.maxOrdinal();
        
        maxShardOrdinal = new int[numShards];
        int minRecordLocationsPerShard = (maxOrdinal + 1) / numShards; 
        for(int i=0;i<numShards;i++)
            maxShardOrdinal[i] = (i < ((maxOrdinal + 1) & (numShards - 1))) ? minRecordLocationsPerShard : minRecordLocationsPerShard - 1;
        
        int maxSetSize = 0;
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        totalOfSetBuckets = new long[numShards];

        for(int i=0;i<=maxOrdinal;i++) {
            if(currentCyclePopulated.get(i) || previousCyclePopulated.get(i)) {
                long pointer = ordinalMap.getPointerForData(i);
                int size = VarInt.readVInt(data, pointer);

                int numBuckets = HashCodes.hashTableSize(size);

                if(size > maxSetSize)
                    maxSetSize = size;

                pointer += VarInt.sizeOfVInt(size);

                int elementOrdinal = 0;

                for(int j=0;j<size;j++) {
                    int elementOrdinalDelta = VarInt.readVInt(data, pointer);
                    elementOrdinal += elementOrdinalDelta;
                    if(elementOrdinal > maxElementOrdinal)
                        maxElementOrdinal = elementOrdinal;
                    pointer += VarInt.sizeOfVInt(elementOrdinalDelta);
                    pointer += VarInt.nextVLongSize(data, pointer);  /// discard hashed bucket
                }

                totalOfSetBuckets[i & (numShards-1)] += numBuckets;
            }
        }
        
        long maxShardTotalOfSetBuckets = 0;
        for(int i=0;i<numShards;i++) {
            if(totalOfSetBuckets[i] > maxShardTotalOfSetBuckets)
                maxShardTotalOfSetBuckets = totalOfSetBuckets[i];
        }

        bitsPerElement = 64 - Long.numberOfLeadingZeros(maxElementOrdinal + 1);
        bitsPerSetSizeValue = 64 - Long.numberOfLeadingZeros(maxSetSize);
        bitsPerSetPointer = 64 - Long.numberOfLeadingZeros(maxShardTotalOfSetBuckets);
    }
    
    private void calculateNumShards() {
        int maxOrdinal = ordinalMap.maxOrdinal();
        int maxSetSize = 0;
        int maxElementOrdinal = 0;
        
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        long totalOfSetBuckets = 0;

        for(int i=0;i<=maxOrdinal;i++) {
            if(currentCyclePopulated.get(i)) {
                long pointer = ordinalMap.getPointerForData(i);
                int size = VarInt.readVInt(data, pointer);

                int numBuckets = HashCodes.hashTableSize(size);

                if(size > maxSetSize)
                    maxSetSize = size;

                pointer += VarInt.sizeOfVInt(size);

                int elementOrdinal = 0;

                for(int j=0;j<size;j++) {
                    int elementOrdinalDelta = VarInt.readVInt(data, pointer);
                    elementOrdinal += elementOrdinalDelta;
                    if(elementOrdinal > maxElementOrdinal)
                        maxElementOrdinal = elementOrdinal;
                    pointer += VarInt.sizeOfVInt(elementOrdinalDelta);
                    pointer += VarInt.nextVLongSize(data, pointer);  /// discard hashed bucket
                }

                totalOfSetBuckets += numBuckets;
            }
        }
        
        long bitsPerElement = 64 - Long.numberOfLeadingZeros(maxElementOrdinal + 1);
        long bitsPerSetSizeValue = 64 - Long.numberOfLeadingZeros(maxSetSize);
        long bitsPerSetPointer = 64 - Long.numberOfLeadingZeros(totalOfSetBuckets);
        
        long projectedSizeOfType = (bitsPerSetSizeValue + bitsPerSetPointer) * (maxOrdinal + 1) / 8;
        projectedSizeOfType += (bitsPerElement * totalOfSetBuckets) / 8;
        
        numShards = 1;
        while(stateEngine.getTargetMaxTypeShardSize() * numShards < projectedSizeOfType) 
            numShards *= 2;
    }

    @Override
    public void calculateSnapshot() {
        maxOrdinal = ordinalMap.maxOrdinal();
        int bitsPerSetFixedLengthPortion = bitsPerSetSizeValue + bitsPerSetPointer;
        
        setPointersAndSizesArray = new FixedLengthElementArray[numShards];
        elementArray = new FixedLengthElementArray[numShards];
        
        for(int i=0;i<numShards;i++) {
            setPointersAndSizesArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerSetFixedLengthPortion * (maxShardOrdinal[i] + 1));
            elementArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerElement * totalOfSetBuckets[i]);
        }

        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        int bucketCounter[] = new int[numShards];
        int shardMask = numShards - 1;

        HollowWriteStateEnginePrimaryKeyHasher primaryKeyHasher = null;

        if(getSchema().getHashKey() != null)
            primaryKeyHasher = new HollowWriteStateEnginePrimaryKeyHasher(getSchema().getHashKey(), getStateEngine());

        for(int ordinal=0;ordinal<=maxOrdinal;ordinal++) {
            int shardNumber = ordinal & shardMask;
            int shardOrdinal = ordinal / numShards;
            
            if(currentCyclePopulated.get(ordinal)) {
                long readPointer = ordinalMap.getPointerForData(ordinal);

                int size = VarInt.readVInt(data, readPointer);
                readPointer += VarInt.sizeOfVInt(size);

                int numBuckets = HashCodes.hashTableSize(size);

                setPointersAndSizesArray[shardNumber].setElementValue(((long)bitsPerSetFixedLengthPortion * shardOrdinal) + bitsPerSetPointer, bitsPerSetSizeValue, size);

                int elementOrdinal = 0;

                for(int j=0;j<numBuckets;j++) {
                    elementArray[shardNumber].setElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + j), bitsPerElement, (1L << bitsPerElement) - 1);
                }

                for(int j=0;j<size;j++) {
                    int elementOrdinalDelta = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(elementOrdinalDelta);
                    int hashedBucket = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(hashedBucket);

                    elementOrdinal += elementOrdinalDelta;

                    if(primaryKeyHasher != null)
                        hashedBucket = primaryKeyHasher.getRecordHash(elementOrdinal) & (numBuckets - 1);

                    while(elementArray[shardNumber].getElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + hashedBucket), bitsPerElement) != ((1L << bitsPerElement) - 1)) {
                        hashedBucket++;
                        hashedBucket &= (numBuckets - 1);
                    }

                    elementArray[shardNumber].clearElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + hashedBucket), bitsPerElement);
                    elementArray[shardNumber].setElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + hashedBucket), bitsPerElement, elementOrdinal);
                }

                bucketCounter[shardNumber] += numBuckets;
            }

            setPointersAndSizesArray[shardNumber].setElementValue((long)bitsPerSetFixedLengthPortion * shardOrdinal, bitsPerSetPointer, bucketCounter[shardNumber]);
        }
    }

    @Override
    public void writeSnapshot(DataOutputStream os) throws IOException {
        /// for unsharded blobs, support pre v2.1.0 clients
        if(numShards == 1) {
            writeSnapshotShard(os, 0);
        } else {
            /// overall max ordinal
            VarInt.writeVInt(os, maxOrdinal);
            
            for(int i=0;i<numShards;i++) {
                writeSnapshotShard(os, i);
            }
        }
        
        /// Populated bits
        currentCyclePopulated.serializeBitsTo(os);

        setPointersAndSizesArray = null;
        elementArray = null;
    }
    
    private void writeSnapshotShard(DataOutputStream os, int shardNumber) throws IOException {
        int bitsPerSetFixedLengthPortion = bitsPerSetSizeValue + bitsPerSetPointer;

        /// 1) max ordinal
        VarInt.writeVInt(os, maxShardOrdinal[shardNumber]);

        /// 2) statistics
        VarInt.writeVInt(os, bitsPerSetPointer);
        VarInt.writeVInt(os, bitsPerSetSizeValue);
        VarInt.writeVInt(os, bitsPerElement);
        VarInt.writeVLong(os, totalOfSetBuckets[shardNumber]);

        /// 3) set pointer array
        int numSetFixedLengthLongs = maxShardOrdinal[shardNumber] == -1 ? 0 : (int)((((long)(maxShardOrdinal[shardNumber] + 1) * bitsPerSetFixedLengthPortion) - 1) / 64) + 1;
        VarInt.writeVInt(os, numSetFixedLengthLongs);
        for(int i=0;i<numSetFixedLengthLongs;i++) {
            os.writeLong(setPointersAndSizesArray[shardNumber].get(i));
        }

        /// 4) element array
        int numElementLongs = totalOfSetBuckets[shardNumber] == 0 ? 0 : (int)(((totalOfSetBuckets[shardNumber] * bitsPerElement) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(elementArray[shardNumber].get(i));
        }
    }

    @Override
    public void calculateDelta() {
        calculateDelta(previousCyclePopulated, currentCyclePopulated);
    }

    @Override
    public void writeDelta(DataOutputStream dos) throws IOException {
        writeCalculatedDelta(dos);
    }

    @Override
    public void calculateReverseDelta() {
        calculateDelta(currentCyclePopulated, previousCyclePopulated);
    }

    @Override
    public void writeReverseDelta(DataOutputStream dos) throws IOException {
        writeCalculatedDelta(dos);
    }

    private void calculateDelta(ThreadSafeBitSet fromCyclePopulated, ThreadSafeBitSet toCyclePopulated) {
        maxOrdinal = ordinalMap.maxOrdinal();
        int bitsPerSetFixedLengthPortion = bitsPerSetSizeValue + bitsPerSetPointer;
        
        numSetsInDelta = new int[numShards];
        numBucketsInDelta = new long[numShards];
        setPointersAndSizesArray = new FixedLengthElementArray[numShards];
        elementArray = new FixedLengthElementArray[numShards];
        deltaAddedOrdinals = new ByteDataBuffer[numShards];
        deltaRemovedOrdinals = new ByteDataBuffer[numShards];

        ThreadSafeBitSet deltaAdditions = toCyclePopulated.andNot(fromCyclePopulated);
        
        int shardMask = numShards - 1;
        
        int addedOrdinal = deltaAdditions.nextSetBit(0);
        while(addedOrdinal != -1) {
            numSetsInDelta[addedOrdinal & shardMask]++;
            long readPointer = ordinalMap.getPointerForData(addedOrdinal);
            int size = VarInt.readVInt(ordinalMap.getByteData().getUnderlyingArray(), readPointer);
            numBucketsInDelta[addedOrdinal & shardMask] += HashCodes.hashTableSize(size);

            addedOrdinal = deltaAdditions.nextSetBit(addedOrdinal + 1);
        }
        
        for(int i=0;i<numShards;i++) {
            setPointersAndSizesArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numSetsInDelta[i] * bitsPerSetFixedLengthPortion);
            elementArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numBucketsInDelta[i] * bitsPerElement);
            deltaAddedOrdinals[i] = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
            deltaRemovedOrdinals[i] = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
        }

        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        int setCounter[] = new int[numShards];
        long bucketCounter[] = new long[numShards];
        int previousRemovedOrdinal[] = new int[numShards];
        int previousAddedOrdinal[] = new int[numShards];

        HollowWriteStateEnginePrimaryKeyHasher primaryKeyHasher = null;

        if(getSchema().getHashKey() != null)
            primaryKeyHasher = new HollowWriteStateEnginePrimaryKeyHasher(getSchema().getHashKey(), getStateEngine());

        for(int ordinal=0;ordinal<=maxOrdinal;ordinal++) {
            int shardNumber = ordinal & shardMask;
            if(deltaAdditions.get(ordinal)) {
                long readPointer = ordinalMap.getPointerForData(ordinal);

                int size = VarInt.readVInt(data, readPointer);
                readPointer += VarInt.sizeOfVInt(size);

                int numBuckets = HashCodes.hashTableSize(size);

                long endBucketPosition = bucketCounter[shardNumber] + numBuckets;

                setPointersAndSizesArray[shardNumber].setElementValue((long)bitsPerSetFixedLengthPortion * setCounter[shardNumber], bitsPerSetPointer, endBucketPosition);
                setPointersAndSizesArray[shardNumber].setElementValue(((long)bitsPerSetFixedLengthPortion * setCounter[shardNumber]) + bitsPerSetPointer, bitsPerSetSizeValue, size);

                int elementOrdinal = 0;

                for(int j=0;j<numBuckets;j++) {
                    elementArray[shardNumber].setElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + j), bitsPerElement, (1L << bitsPerElement) - 1);
                }

                for(int j=0;j<size;j++) {
                    int elementOrdinalDelta = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(elementOrdinalDelta);
                    int hashedBucket = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(hashedBucket);
                    elementOrdinal += elementOrdinalDelta;

                    if(primaryKeyHasher != null)
                        hashedBucket = primaryKeyHasher.getRecordHash(elementOrdinal) & (numBuckets - 1);

                    while(elementArray[shardNumber].getElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + hashedBucket), bitsPerElement) != ((1L << bitsPerElement) - 1)) {
                        hashedBucket++;
                        hashedBucket &= (numBuckets - 1);
                    }

                    elementArray[shardNumber].clearElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + hashedBucket), bitsPerElement);
                    elementArray[shardNumber].setElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + hashedBucket), bitsPerElement, elementOrdinal);
                }

                bucketCounter[shardNumber] += numBuckets;
                setCounter[shardNumber]++;

                int shardOrdinal = ordinal / numShards;
                VarInt.writeVInt(deltaAddedOrdinals[shardNumber], shardOrdinal - previousAddedOrdinal[shardNumber]);
                previousAddedOrdinal[shardNumber] = shardOrdinal;
            } else if(fromCyclePopulated.get(ordinal) && !toCyclePopulated.get(ordinal)) {
                int shardOrdinal = ordinal / numShards;
                VarInt.writeVInt(deltaRemovedOrdinals[shardNumber], shardOrdinal - previousRemovedOrdinal[shardNumber]);
                previousRemovedOrdinal[shardNumber] = shardOrdinal;
            }
        }
    }

    @Override
    public void calculateRadialDelta() {
        maxOrdinal = ordinalMap.maxOrdinal();
        int bitsPerSetFixedLengthPortion = bitsPerSetSizeValue + bitsPerSetPointer;
        
        numSetsInDelta = new int[numShards];
        numBucketsInDelta = new long[numShards];
        setPointersAndSizesArray = new FixedLengthElementArray[numShards];
        elementArray = new FixedLengthElementArray[numShards];
        deltaAddedOrdinals = new ByteDataBuffer[numShards];
        deltaRemovedOrdinals = new ByteDataBuffer[numShards];

        ThreadSafeBitSet deltaAdditions = currentCyclePopulated.and(removedSinceHubState);
        int ordinal = currentCyclePopulated.nextSetBit(hubStateMaxOrdinal + 1);
        while(ordinal != -1) {
            deltaAdditions.set(ordinal);
            ordinal = currentCyclePopulated.nextSetBit(ordinal + 1);
        }

        int shardMask = numShards - 1;
        
        int addedOrdinal = deltaAdditions.nextSetBit(0);
        while(addedOrdinal != -1) {
            numSetsInDelta[addedOrdinal & shardMask]++;
            long readPointer = ordinalMap.getPointerForData(addedOrdinal);
            int size = VarInt.readVInt(ordinalMap.getByteData().getUnderlyingArray(), readPointer);
            numBucketsInDelta[addedOrdinal & shardMask] += HashCodes.hashTableSize(size);

            addedOrdinal = deltaAdditions.nextSetBit(addedOrdinal + 1);
        }
        
        for(int i=0;i<numShards;i++) {
            setPointersAndSizesArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numSetsInDelta[i] * bitsPerSetFixedLengthPortion);
            elementArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numBucketsInDelta[i] * bitsPerElement);
            deltaAddedOrdinals[i] = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
            deltaRemovedOrdinals[i] = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
        }

        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        int setCounter[] = new int[numShards];
        long bucketCounter[] = new long[numShards];
        int previousRemovedOrdinal[] = new int[numShards];
        int previousAddedOrdinal[] = new int[numShards];

        HollowWriteStateEnginePrimaryKeyHasher primaryKeyHasher = null;

        if(getSchema().getHashKey() != null)
            primaryKeyHasher = new HollowWriteStateEnginePrimaryKeyHasher(getSchema().getHashKey(), getStateEngine());

        for(ordinal=0;ordinal<=maxOrdinal;ordinal++) {
            int shardNumber = ordinal & shardMask;
            if(deltaAdditions.get(ordinal)) {
                long readPointer = ordinalMap.getPointerForData(ordinal);

                int size = VarInt.readVInt(data, readPointer);
                readPointer += VarInt.sizeOfVInt(size);

                int numBuckets = HashCodes.hashTableSize(size);

                long endBucketPosition = bucketCounter[shardNumber] + numBuckets;

                setPointersAndSizesArray[shardNumber].setElementValue((long)bitsPerSetFixedLengthPortion * setCounter[shardNumber], bitsPerSetPointer, endBucketPosition);
                setPointersAndSizesArray[shardNumber].setElementValue(((long)bitsPerSetFixedLengthPortion * setCounter[shardNumber]) + bitsPerSetPointer, bitsPerSetSizeValue, size);

                int elementOrdinal = 0;

                for(int j=0;j<numBuckets;j++) {
                    elementArray[shardNumber].setElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + j), bitsPerElement, (1L << bitsPerElement) - 1);
                }

                for(int j=0;j<size;j++) {
                    int elementOrdinalDelta = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(elementOrdinalDelta);
                    int hashedBucket = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(hashedBucket);
                    elementOrdinal += elementOrdinalDelta;

                    if(primaryKeyHasher != null)
                        hashedBucket = primaryKeyHasher.getRecordHash(elementOrdinal) & (numBuckets - 1);

                    while(elementArray[shardNumber].getElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + hashedBucket), bitsPerElement) != ((1L << bitsPerElement) - 1)) {
                        hashedBucket++;
                        hashedBucket &= (numBuckets - 1);
                    }

                    elementArray[shardNumber].clearElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + hashedBucket), bitsPerElement);
                    elementArray[shardNumber].setElementValue((long)bitsPerElement * (bucketCounter[shardNumber] + hashedBucket), bitsPerElement, elementOrdinal);
                }

                bucketCounter[shardNumber] += numBuckets;
                setCounter[shardNumber]++;

                int shardOrdinal = ordinal / numShards;
                VarInt.writeVInt(deltaAddedOrdinals[shardNumber], shardOrdinal - previousAddedOrdinal[shardNumber]);
                previousAddedOrdinal[shardNumber] = shardOrdinal;
            } 

            if(removedSinceHubState.get(ordinal) && ordinal <= hubStateMaxOrdinal) {
                int shardOrdinal = ordinal / numShards;
                VarInt.writeVInt(deltaRemovedOrdinals[shardNumber], shardOrdinal - previousRemovedOrdinal[shardNumber]);
                previousRemovedOrdinal[shardNumber] = shardOrdinal;
            }
        }
    }

    @Override
    public void writeRadialDelta(DataOutputStream dos) throws IOException {
        writeCalculatedDelta(dos);
    }

    private void writeCalculatedDelta(DataOutputStream os) throws IOException {
        /// for unsharded blobs, support pre v2.1.0 clients
        if(numShards == 1) {
            writeCalculatedDeltaShard(os, 0);
        } else {
            /// overall max ordinal
            VarInt.writeVInt(os, maxOrdinal);
            
            for(int i=0;i<numShards;i++) {
                writeCalculatedDeltaShard(os, i);
            }
        }
        
        setPointersAndSizesArray = null;
        elementArray = null;
        deltaAddedOrdinals = null;
        deltaRemovedOrdinals = null;
    }
    
    private void writeCalculatedDeltaShard(DataOutputStream os, int shardNumber) throws IOException {
        
        int bitsPerSetFixedLengthPortion = bitsPerSetSizeValue + bitsPerSetPointer;

        /// 1) max ordinal
        VarInt.writeVInt(os, maxShardOrdinal[shardNumber]);

        /// 2) removal / addition ordinals.
        VarInt.writeVLong(os, deltaRemovedOrdinals[shardNumber].length());
        deltaRemovedOrdinals[shardNumber].getUnderlyingArray().writeTo(os, 0, deltaRemovedOrdinals[shardNumber].length());
        VarInt.writeVLong(os, deltaAddedOrdinals[shardNumber].length());
        deltaAddedOrdinals[shardNumber].getUnderlyingArray().writeTo(os, 0, deltaAddedOrdinals[shardNumber].length());

        /// 3) statistics
        VarInt.writeVInt(os, bitsPerSetPointer);
        VarInt.writeVInt(os, bitsPerSetSizeValue);
        VarInt.writeVInt(os, bitsPerElement);
        VarInt.writeVLong(os, totalOfSetBuckets[shardNumber]);

        /// 4) set pointer array
        int numSetFixedLengthLongs = numSetsInDelta[shardNumber] == 0 ? 0 : (int)((((long)numSetsInDelta[shardNumber] * bitsPerSetFixedLengthPortion) - 1) / 64) + 1;
        VarInt.writeVInt(os, numSetFixedLengthLongs);
        for(int i=0;i<numSetFixedLengthLongs;i++) {
            os.writeLong(setPointersAndSizesArray[shardNumber].get(i));
        }

        /// 5) element array
        int numElementLongs = numBucketsInDelta[shardNumber] == 0 ? 0 : (int)(((numBucketsInDelta[shardNumber] * bitsPerElement) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(elementArray[shardNumber].get(i));
        }
    }

}
