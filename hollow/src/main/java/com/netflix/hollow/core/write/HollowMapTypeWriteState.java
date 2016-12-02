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

import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.io.DataOutputStream;
import java.io.IOException;

public class HollowMapTypeWriteState extends HollowTypeWriteState {

    /// statistics required for writing fixed length set data
    private int bitsPerMapPointer;
    private int bitsPerMapSizeValue;
    private int bitsPerKeyElement;
    private int bitsPerValueElement;
    private long totalOfMapBuckets;

    /// data required for writing snapshot or delta
    private int maxOrdinal;
    private FixedLengthElementArray mapPointersAndSizesArray;
    private FixedLengthElementArray entryArray;

    /// additional data required for writing delta
    private int numMapsInDelta;
    private long numBucketsInDelta;
    private ByteDataBuffer deltaAddedOrdinals;
    private ByteDataBuffer deltaRemovedOrdinals;

    public HollowMapTypeWriteState(HollowMapSchema schema) {
        super(schema);
    }

    @Override
    public HollowMapSchema getSchema() {
        return (HollowMapSchema)schema;
    }

    @Override
    public void prepareForWrite() {
        super.prepareForWrite();

        totalOfMapBuckets = 0;

        gatherStatistics();
    }

    private void gatherStatistics() {
        int maxKeyOrdinal = 0;
        int maxValueOrdinal = 0;

        int maxOrdinal = ordinalMap.maxOrdinal();
        int maxMapSize = 0;
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();


        for(int i=0;i<=maxOrdinal;i++) {
            if(currentCyclePopulated.get(i) || previousCyclePopulated.get(i)) {
                long pointer = ordinalMap.getPointerForData(i);
                int size = VarInt.readVInt(data, pointer);

                int numBuckets = HashCodes.hashTableSize(size);

                if(size > maxMapSize)
                    maxMapSize = size;

                pointer += VarInt.sizeOfVInt(size);

                int keyOrdinal = 0;

                for(int j=0;j<size;j++) {
                    int keyOrdinalDelta = VarInt.readVInt(data, pointer);
                    pointer += VarInt.sizeOfVInt(keyOrdinalDelta);
                    int valueOrdinal = VarInt.readVInt(data, pointer);
                    pointer += VarInt.sizeOfVInt(valueOrdinal);

                    keyOrdinal += keyOrdinalDelta;
                    if(keyOrdinal > maxKeyOrdinal)
                        maxKeyOrdinal = keyOrdinal;
                    if(valueOrdinal > maxValueOrdinal)
                        maxValueOrdinal = valueOrdinal;

                    pointer += VarInt.nextVLongSize(data, pointer);  /// discard hashed bucket
                }

                totalOfMapBuckets += numBuckets;
            }
        }

        bitsPerKeyElement = 64 - Long.numberOfLeadingZeros(maxKeyOrdinal + 1);
        bitsPerValueElement = 64 - Long.numberOfLeadingZeros(maxValueOrdinal);

        bitsPerMapSizeValue = 64 - Long.numberOfLeadingZeros(maxMapSize);

        bitsPerMapPointer = 64 - Long.numberOfLeadingZeros(totalOfMapBuckets);
    }

    @Override
    public void calculateSnapshot() {
        maxOrdinal = ordinalMap.maxOrdinal();
        int bitsPerMapFixedLengthPortion = bitsPerMapSizeValue + bitsPerMapPointer;
        int bitsPerMapEntry = bitsPerKeyElement + bitsPerValueElement;

        mapPointersAndSizesArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerMapFixedLengthPortion * (maxOrdinal + 1));
        entryArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerMapEntry * totalOfMapBuckets);

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

                mapPointersAndSizesArray.setElementValue(((long)bitsPerMapFixedLengthPortion * i) + bitsPerMapPointer, bitsPerMapSizeValue, size);

                int keyElementOrdinal = 0;

                for(int j=0;j<numBuckets;j++) {
                    entryArray.setElementValue((long)bitsPerMapEntry * (bucketCounter + j), bitsPerKeyElement, (1L << bitsPerKeyElement) - 1);
                }

                for(int j=0;j<size;j++) {
                    int keyElementOrdinalDelta = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(keyElementOrdinalDelta);
                    int valueElementOrdinal = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(valueElementOrdinal);
                    int hashedBucket = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(hashedBucket);

                    keyElementOrdinal += keyElementOrdinalDelta;

                    if(primaryKeyHasher != null)
                        hashedBucket = primaryKeyHasher.getRecordHash(keyElementOrdinal) & (numBuckets - 1);

                    while(entryArray.getElementValue((long)bitsPerMapEntry * (bucketCounter + hashedBucket), bitsPerKeyElement) != ((1L << bitsPerKeyElement) - 1)) {
                        hashedBucket++;
                        hashedBucket &= (numBuckets - 1);
                    }

                    long mapEntryBitOffset = bitsPerMapEntry * (bucketCounter + hashedBucket);
                    entryArray.clearElementValue(mapEntryBitOffset, bitsPerMapEntry);
                    entryArray.setElementValue(mapEntryBitOffset, bitsPerKeyElement, keyElementOrdinal);
                    entryArray.setElementValue(mapEntryBitOffset + bitsPerKeyElement, bitsPerValueElement, valueElementOrdinal);
                }

                bucketCounter += numBuckets;
            }

            mapPointersAndSizesArray.setElementValue((long)bitsPerMapFixedLengthPortion * i, bitsPerMapPointer, bucketCounter);
        }
    }

    @Override
    public void writeSnapshot(DataOutputStream os) throws IOException {
        int bitsPerMapFixedLengthPortion = bitsPerMapSizeValue + bitsPerMapPointer;
        int bitsPerMapEntry = bitsPerKeyElement + bitsPerValueElement;

        /// 1) max ordinal
        VarInt.writeVInt(os, maxOrdinal);

        /// 2) statistics
        VarInt.writeVInt(os, bitsPerMapPointer);
        VarInt.writeVInt(os, bitsPerMapSizeValue);
        VarInt.writeVInt(os, bitsPerKeyElement);
        VarInt.writeVInt(os, bitsPerValueElement);
        VarInt.writeVLong(os, totalOfMapBuckets);

        /// 3) list pointer array
        int numMapFixedLengthLongs = maxOrdinal == -1 ? 0 : (int)((((long)(maxOrdinal + 1) * bitsPerMapFixedLengthPortion) - 1) / 64) + 1;
        VarInt.writeVInt(os, numMapFixedLengthLongs);
        for(int i=0;i<numMapFixedLengthLongs;i++) {
            os.writeLong(mapPointersAndSizesArray.get(i));
        }

        /// 4) element array
        int numElementLongs = totalOfMapBuckets == 0 ? 0 : (int)(((totalOfMapBuckets * bitsPerMapEntry) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(entryArray.get(i));
        }

        /// 5) Populated bits
        currentCyclePopulated.serializeBitsTo(os);

        mapPointersAndSizesArray = null;
        entryArray = null;
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
        int bitsPerMapFixedLengthPortion = bitsPerMapSizeValue + bitsPerMapPointer;
        int bitsPerMapEntry = bitsPerKeyElement + bitsPerValueElement;

        ThreadSafeBitSet deltaAdditions = toCyclePopulated.andNot(fromCyclePopulated);
        numMapsInDelta = deltaAdditions.cardinality();
        numBucketsInDelta = calculateNumBucketsInDelta(maxOrdinal, deltaAdditions);

        mapPointersAndSizesArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numMapsInDelta * bitsPerMapFixedLengthPortion);
        entryArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, numBucketsInDelta * bitsPerMapEntry);
        deltaAddedOrdinals = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
        deltaRemovedOrdinals = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);

        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        int mapCounter = 0;
        int bucketCounter = 0;
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

                mapPointersAndSizesArray.setElementValue((long)bitsPerMapFixedLengthPortion * mapCounter, bitsPerMapPointer, endBucketPosition);
                mapPointersAndSizesArray.setElementValue(((long)bitsPerMapFixedLengthPortion * mapCounter) + bitsPerMapPointer, bitsPerMapSizeValue, size);

                int keyElementOrdinal = 0;

                for(int j=0;j<numBuckets;j++) {
                    entryArray.setElementValue((long)bitsPerMapEntry * (bucketCounter + j), bitsPerKeyElement, (1L << bitsPerKeyElement) - 1);
                }

                for(int j=0;j<size;j++) {
                    int keyElementOrdinalDelta = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(keyElementOrdinalDelta);
                    int valueElementOrdinal = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(valueElementOrdinal);
                    int hashedBucket = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(hashedBucket);

                    keyElementOrdinal += keyElementOrdinalDelta;

                    if(primaryKeyHasher != null)
                        hashedBucket = primaryKeyHasher.getRecordHash(keyElementOrdinal) & (numBuckets - 1);

                    while(entryArray.getElementValue((long)bitsPerMapEntry * (bucketCounter + hashedBucket), bitsPerKeyElement) != ((1L << bitsPerKeyElement) - 1)) {
                        hashedBucket++;
                        hashedBucket &= (numBuckets - 1);
                    }

                    long mapEntryBitOffset = bitsPerMapEntry * (bucketCounter + hashedBucket);
                    entryArray.clearElementValue(mapEntryBitOffset, bitsPerMapEntry);
                    entryArray.setElementValue(mapEntryBitOffset, bitsPerKeyElement, keyElementOrdinal);
                    entryArray.setElementValue(mapEntryBitOffset + bitsPerKeyElement, bitsPerValueElement, valueElementOrdinal);
                }

                bucketCounter += numBuckets;
                mapCounter++;

                VarInt.writeVInt(deltaAddedOrdinals, i - previousAddedOrdinal);
                previousAddedOrdinal = i;
            } else if(fromCyclePopulated.get(i) && !toCyclePopulated.get(i)) {
                VarInt.writeVInt(deltaRemovedOrdinals, i - previousRemovedOrdinal);
                previousRemovedOrdinal = i;
            }
        }
    }

    private void writeCalculatedDelta(DataOutputStream os) throws IOException {
        int bitsPerMapFixedLengthPortion = bitsPerMapSizeValue + bitsPerMapPointer;
        int bitsPerMapEntry = bitsPerKeyElement + bitsPerValueElement;

        /// 1) max ordinal
        VarInt.writeVInt(os, maxOrdinal);

        /// 2) removal / addition ordinals.
        VarInt.writeVLong(os, deltaRemovedOrdinals.length());
        deltaRemovedOrdinals.getUnderlyingArray().writeTo(os, 0, deltaRemovedOrdinals.length());
        VarInt.writeVLong(os, deltaAddedOrdinals.length());
        deltaAddedOrdinals.getUnderlyingArray().writeTo(os, 0, deltaAddedOrdinals.length());

        /// 3) statistics
        VarInt.writeVInt(os, bitsPerMapPointer);
        VarInt.writeVInt(os, bitsPerMapSizeValue);
        VarInt.writeVInt(os, bitsPerKeyElement);
        VarInt.writeVInt(os, bitsPerValueElement);
        VarInt.writeVLong(os, totalOfMapBuckets);

        /// 4) pointer array
        int numMapFixedLengthLongs = numMapsInDelta == 0 ? 0 : (int)((((long)numMapsInDelta * bitsPerMapFixedLengthPortion) - 1) / 64) + 1;
        VarInt.writeVInt(os, numMapFixedLengthLongs);
        for(int i=0;i<numMapFixedLengthLongs;i++) {
            os.writeLong(mapPointersAndSizesArray.get(i));
        }

        /// 5) element array
        int numElementLongs = numBucketsInDelta == 0 ? 0 : (int)(((numBucketsInDelta * bitsPerMapEntry) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(entryArray.get(i));
        }

        mapPointersAndSizesArray = null;
        entryArray = null;
        deltaAddedOrdinals = null;
        deltaRemovedOrdinals = null;
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
