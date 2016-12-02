/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;

import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.io.DataOutputStream;
import java.io.IOException;

public class HollowSetTypeWriteState extends HollowTypeWriteState {

    /// statistics required for writing fixed length set data
    private int bitsPerSetPointer;
    private int bitsPerElement;
    private int bitsPerSetSizeValue;
    private long totalOfSetBuckets;

    /// data required for writing snapshot or delta
    private int maxOrdinal;
    private FixedLengthElementArray setPointersAndSizesArray;
    private FixedLengthElementArray elementArray;

    /// additional data required for writing delta
    private int numSetsInDelta;
    private long numBucketsInDelta;
    private ByteDataBuffer addedOrdinals;
    private ByteDataBuffer removedOrdinals;

    public HollowSetTypeWriteState(HollowSetSchema schema) {
        super(schema);
    }

    @Override
    public HollowSetSchema getSchema() {
        return (HollowSetSchema)schema;
    }

    public void prepareForWrite() {
        super.prepareForWrite();

        totalOfSetBuckets = 0;

        gatherStatistics();
    }

    private void gatherStatistics() {
        int maxElementOrdinal = 0;

        int maxOrdinal = ordinalMap.maxOrdinal();
        int maxSetSize = 0;
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();


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

                totalOfSetBuckets += numBuckets;
            }
        }

        bitsPerElement = 64 - Long.numberOfLeadingZeros(maxElementOrdinal + 1);
        bitsPerSetSizeValue = 64 - Long.numberOfLeadingZeros(maxSetSize);
        bitsPerSetPointer = 64 - Long.numberOfLeadingZeros(totalOfSetBuckets);
    }

    @Override
    public void calculateSnapshot() {
        maxOrdinal = ordinalMap.maxOrdinal();
        int bitsPerSetFixedLengthPortion = bitsPerSetSizeValue + bitsPerSetPointer;

        setPointersAndSizesArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerSetFixedLengthPortion * (maxOrdinal + 1));
        elementArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerElement * totalOfSetBuckets);

        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        int bucketCounter = 0;

        HollowWriteStateEnginePrimaryKeyHasher primaryKeyHasher = null;

        if(getSchema().getHashKey() != null)
            primaryKeyHasher = new HollowWriteStateEnginePrimaryKeyHasher(getSchema().getHashKey(), getStateEngine());

        for(int i=0;i<=maxOrdinal;i++) {
            if(currentCyclePopulated.get(i)) {
                long readPointer = ordinalMap.getPointerForData(i);

                int size = VarInt.readVInt(data, readPointer);
                readPointer += VarInt.sizeOfVInt(size);

                int numBuckets = HashCodes.hashTableSize(size);

                setPointersAndSizesArray.setElementValue(((long)bitsPerSetFixedLengthPortion * i) + bitsPerSetPointer, bitsPerSetSizeValue, size);

                int elementOrdinal = 0;

                for(int j=0;j<numBuckets;j++) {
                    elementArray.setElementValue((long)bitsPerElement * (bucketCounter + j), bitsPerElement, (1L << bitsPerElement) - 1);
                }

                for(int j=0;j<size;j++) {
                    int elementOrdinalDelta = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(elementOrdinalDelta);
                    int hashedBucket = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(hashedBucket);

                    elementOrdinal += elementOrdinalDelta;

                    if(primaryKeyHasher != null)
                        hashedBucket = primaryKeyHasher.getRecordHash(elementOrdinal) & (numBuckets - 1);

                    while(elementArray.getElementValue((long)bitsPerElement * (bucketCounter + hashedBucket), bitsPerElement) != ((1L << bitsPerElement) - 1)) {
                        hashedBucket++;
                        hashedBucket &= (numBuckets - 1);
                    }

                    elementArray.clearElementValue((long)bitsPerElement * (bucketCounter + hashedBucket), bitsPerElement);
                    elementArray.setElementValue((long)bitsPerElement * (bucketCounter + hashedBucket), bitsPerElement, elementOrdinal);
                }

                bucketCounter += numBuckets;
            }

            setPointersAndSizesArray.setElementValue((long)bitsPerSetFixedLengthPortion * i, bitsPerSetPointer, bucketCounter);
        }
    }

    @Override
    public void writeSnapshot(DataOutputStream os) throws IOException {
        int bitsPerSetFixedLengthPortion = bitsPerSetSizeValue + bitsPerSetPointer;

        /// 1) max ordinal
        VarInt.writeVInt(os, maxOrdinal);

        /// 2) statistics
        VarInt.writeVInt(os, bitsPerSetPointer);
        VarInt.writeVInt(os, bitsPerSetSizeValue);
        VarInt.writeVInt(os, bitsPerElement);
        VarInt.writeVLong(os, totalOfSetBuckets);

        /// 3) set pointer array
        int numSetFixedLengthLongs = maxOrdinal == -1 ? 0 : (int)((((long)(maxOrdinal + 1) * bitsPerSetFixedLengthPortion) - 1) / 64) + 1;
        VarInt.writeVInt(os, numSetFixedLengthLongs);
        for(int i=0;i<numSetFixedLengthLongs;i++) {
            os.writeLong(setPointersAndSizesArray.get(i));
        }

        /// 4) element array
        int numElementLongs = totalOfSetBuckets == 0 ? 0 : (int)(((totalOfSetBuckets * bitsPerElement) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(elementArray.get(i));
        }

        /// 5) Populated bits
        currentCyclePopulated.serializeBitsTo(os);

        setPointersAndSizesArray = null;
        elementArray = null;
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

    public void calculateDelta(ThreadSafeBitSet fromCyclePopulated, ThreadSafeBitSet toCyclePopulated) {
        maxOrdinal = ordinalMap.maxOrdinal();
        int bitsPerSetFixedLengthPortion = bitsPerSetSizeValue + bitsPerSetPointer;

        ThreadSafeBitSet deltaAdditions = toCyclePopulated.andNot(fromCyclePopulated);
        numSetsInDelta = deltaAdditions.cardinality();
        numBucketsInDelta = calculateNumBucketsInDelta(maxOrdinal, deltaAdditions);

        setPointersAndSizesArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numSetsInDelta * bitsPerSetFixedLengthPortion);
        elementArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numBucketsInDelta * bitsPerElement);
        addedOrdinals = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
        removedOrdinals = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);

        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        int bucketCounter = 0;
        int setCounter = 0;
        int previousRemovedOrdinal = 0;
        int previousAddedOrdinal = 0;

        HollowWriteStateEnginePrimaryKeyHasher primaryKeyHasher = null;

        if(getSchema().getHashKey() != null)
            primaryKeyHasher = new HollowWriteStateEnginePrimaryKeyHasher(getSchema().getHashKey(), getStateEngine());

        for(int i=0;i<=maxOrdinal;i++) {
            if(toCyclePopulated.get(i) && !fromCyclePopulated.get(i)) {
                long readPointer = ordinalMap.getPointerForData(i);

                int size = VarInt.readVInt(data, readPointer);
                readPointer += VarInt.sizeOfVInt(size);

                int numBuckets = HashCodes.hashTableSize(size);

                long endBucketPosition = bucketCounter + numBuckets;

                setPointersAndSizesArray.setElementValue((long)bitsPerSetFixedLengthPortion * setCounter, bitsPerSetPointer, endBucketPosition);
                setPointersAndSizesArray.setElementValue(((long)bitsPerSetFixedLengthPortion * setCounter) + bitsPerSetPointer, bitsPerSetSizeValue, size);

                int elementOrdinal = 0;

                for(int j=0;j<numBuckets;j++) {
                    elementArray.setElementValue((long)bitsPerElement * (bucketCounter + j), bitsPerElement, (1L << bitsPerElement) - 1);
                }

                for(int j=0;j<size;j++) {
                    int elementOrdinalDelta = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(elementOrdinalDelta);
                    int hashedBucket = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(hashedBucket);
                    elementOrdinal += elementOrdinalDelta;

                    if(primaryKeyHasher != null)
                        hashedBucket = primaryKeyHasher.getRecordHash(elementOrdinal) & (numBuckets - 1);

                    while(elementArray.getElementValue((long)bitsPerElement * (bucketCounter + hashedBucket), bitsPerElement) != ((1L << bitsPerElement) - 1)) {
                        hashedBucket++;
                        hashedBucket &= (numBuckets - 1);
                    }

                    elementArray.clearElementValue((long)bitsPerElement * (bucketCounter + hashedBucket), bitsPerElement);
                    elementArray.setElementValue((long)bitsPerElement * (bucketCounter + hashedBucket), bitsPerElement, elementOrdinal);
                }

                bucketCounter += numBuckets;
                setCounter++;

                VarInt.writeVInt(addedOrdinals, i - previousAddedOrdinal);
                previousAddedOrdinal = i;
            } else if(fromCyclePopulated.get(i) && !toCyclePopulated.get(i)) {
                VarInt.writeVInt(removedOrdinals, i - previousRemovedOrdinal);
                previousRemovedOrdinal = i;
            }
        }
    }

    public void writeCalculatedDelta(DataOutputStream os) throws IOException {
        int bitsPerSetFixedLengthPortion = bitsPerSetSizeValue + bitsPerSetPointer;

        /// 1) max ordinal
        VarInt.writeVInt(os, maxOrdinal);

        /// 2) removal / addition ordinals.
        VarInt.writeVLong(os, removedOrdinals.length());
        removedOrdinals.getUnderlyingArray().writeTo(os, 0, removedOrdinals.length());
        VarInt.writeVLong(os, addedOrdinals.length());
        addedOrdinals.getUnderlyingArray().writeTo(os, 0, addedOrdinals.length());

        /// 3) statistics
        VarInt.writeVInt(os, bitsPerSetPointer);
        VarInt.writeVInt(os, bitsPerSetSizeValue);
        VarInt.writeVInt(os, bitsPerElement);
        VarInt.writeVLong(os, totalOfSetBuckets);

        /// 4) set pointer array
        int numSetFixedLengthLongs = numSetsInDelta == 0 ? 0 : (int)((((long)numSetsInDelta * bitsPerSetFixedLengthPortion) - 1) / 64) + 1;
        VarInt.writeVInt(os, numSetFixedLengthLongs);
        for(int i=0;i<numSetFixedLengthLongs;i++) {
            os.writeLong(setPointersAndSizesArray.get(i));
        }

        /// 5) element array
        int numElementLongs = numBucketsInDelta == 0 ? 0 : (int)(((numBucketsInDelta * bitsPerElement) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(elementArray.get(i));
        }

        setPointersAndSizesArray = null;
        elementArray = null;
        addedOrdinals = null;
        removedOrdinals = null;
    }

    private long calculateNumBucketsInDelta(int maxOrdinal, ThreadSafeBitSet deltaAdditions) {
        long totalNumberOfBuckets = 0;

        for(int i=0;i<=maxOrdinal;i++) {
            if(deltaAdditions.get(i)) {
                long readPointer = ordinalMap.getPointerForData(i);
                int size = VarInt.readVInt(ordinalMap.getByteData().getUnderlyingArray(), readPointer);

                totalNumberOfBuckets += HashCodes.hashTableSize(size);
            }
        }

        return totalNumberOfBuckets;
    }

}
