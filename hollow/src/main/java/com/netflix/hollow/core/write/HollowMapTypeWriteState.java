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
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.schema.HollowMapSchema;
import java.io.DataOutputStream;
import java.io.IOException;

public class HollowMapTypeWriteState extends HollowTypeWriteState {

    /// statistics required for writing fixed length set data
    private int bitsPerMapPointer;
    private int bitsPerMapSizeValue;
    private int bitsPerKeyElement;
    private int bitsPerValueElement;
    private long totalOfMapBuckets[];

    /// data required for writing snapshot or delta
    private int maxOrdinal;
    private int maxShardOrdinal[];
    private FixedLengthElementArray mapPointersAndSizesArray[];
    private FixedLengthElementArray entryData[];

    /// additional data required for writing delta
    private int numMapsInDelta[];
    private long numBucketsInDelta[];
    private ByteDataBuffer deltaAddedOrdinals[];
    private ByteDataBuffer deltaRemovedOrdinals[];

    public HollowMapTypeWriteState(HollowMapSchema schema) {
        this(schema, -1);
    }
    
    public HollowMapTypeWriteState(HollowMapSchema schema, int numShards) {
        super(schema, numShards);
    }

    @Override
    public HollowMapSchema getSchema() {
        return (HollowMapSchema)schema;
    }

    @Override
    public void prepareForWrite() {
        super.prepareForWrite();

        gatherStatistics();
    }

    private void gatherStatistics() {
        if(numShards == -1)
            calculateNumShards();
        
        int maxKeyOrdinal = 0;
        int maxValueOrdinal = 0;

        int maxOrdinal = ordinalMap.maxOrdinal();

        maxShardOrdinal = new int[numShards];
        int minRecordLocationsPerShard = (maxOrdinal + 1) / numShards; 
        for(int i=0;i<numShards;i++)
            maxShardOrdinal[i] = (i < ((maxOrdinal + 1) & (numShards - 1))) ? minRecordLocationsPerShard : minRecordLocationsPerShard - 1;
        
        int maxMapSize = 0;
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        totalOfMapBuckets = new long[numShards];
        
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

                totalOfMapBuckets[i & (numShards-1)] += numBuckets;
            }
        }
        
        long maxShardTotalOfMapBuckets = 0;
        for(int i=0;i<numShards;i++) {
            if(totalOfMapBuckets[i] > maxShardTotalOfMapBuckets)
                maxShardTotalOfMapBuckets = totalOfMapBuckets[i];
        }

        bitsPerKeyElement = 64 - Long.numberOfLeadingZeros(maxKeyOrdinal + 1);
        bitsPerValueElement = 64 - Long.numberOfLeadingZeros(maxValueOrdinal);

        bitsPerMapSizeValue = 64 - Long.numberOfLeadingZeros(maxMapSize);

        bitsPerMapPointer = 64 - Long.numberOfLeadingZeros(maxShardTotalOfMapBuckets);
    }
    
    private void calculateNumShards() {
        int maxKeyOrdinal = 0;
        int maxValueOrdinal = 0;
        int maxOrdinal = ordinalMap.maxOrdinal();

        int maxMapSize = 0;
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        long totalOfMapBuckets = 0;
        
        for(int i=0;i<=maxOrdinal;i++) {
            if(currentCyclePopulated.get(i)) {
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

        long bitsPerKeyElement = 64 - Long.numberOfLeadingZeros(maxKeyOrdinal + 1);
        long bitsPerValueElement = 64 - Long.numberOfLeadingZeros(maxValueOrdinal);
        long bitsPerMapSizeValue = 64 - Long.numberOfLeadingZeros(maxMapSize);
        long bitsPerMapPointer = 64 - Long.numberOfLeadingZeros(totalOfMapBuckets);
        
        long projectedSizeOfType = (bitsPerMapSizeValue + bitsPerMapPointer) * (maxOrdinal + 1) / 8;
        projectedSizeOfType += ((bitsPerKeyElement + bitsPerValueElement) * totalOfMapBuckets) / 8;
        
        numShards = 1;
        while(stateEngine.getTargetMaxTypeShardSize() * numShards < projectedSizeOfType) 
            numShards *= 2;
    }

    @Override
    public void calculateSnapshot() {
        maxOrdinal = ordinalMap.maxOrdinal();
        int bitsPerMapFixedLengthPortion = bitsPerMapSizeValue + bitsPerMapPointer;
        int bitsPerMapEntry = bitsPerKeyElement + bitsPerValueElement;
        
        mapPointersAndSizesArray = new FixedLengthElementArray[numShards];
        entryData = new FixedLengthElementArray[numShards];

        for(int i=0;i<numShards;i++) {
            mapPointersAndSizesArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerMapFixedLengthPortion * (maxShardOrdinal[i] + 1));
            entryData[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerMapEntry * totalOfMapBuckets[i]);
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

                mapPointersAndSizesArray[shardNumber].setElementValue(((long)bitsPerMapFixedLengthPortion * shardOrdinal) + bitsPerMapPointer, bitsPerMapSizeValue, size);

                int keyElementOrdinal = 0;

                for(int j=0;j<numBuckets;j++) {
                    entryData[shardNumber].setElementValue((long)bitsPerMapEntry * (bucketCounter[shardNumber] + j), bitsPerKeyElement, (1L << bitsPerKeyElement) - 1);
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

                    while(entryData[shardNumber].getElementValue((long)bitsPerMapEntry * (bucketCounter[shardNumber] + hashedBucket), bitsPerKeyElement) != ((1L << bitsPerKeyElement) - 1)) {
                        hashedBucket++;
                        hashedBucket &= (numBuckets - 1);
                    }

                    long mapEntryBitOffset = (long)bitsPerMapEntry * (bucketCounter[shardNumber] + hashedBucket);
                    entryData[shardNumber].clearElementValue(mapEntryBitOffset, bitsPerMapEntry);
                    entryData[shardNumber].setElementValue(mapEntryBitOffset, bitsPerKeyElement, keyElementOrdinal);
                    entryData[shardNumber].setElementValue(mapEntryBitOffset + bitsPerKeyElement, bitsPerValueElement, valueElementOrdinal);
                }

                bucketCounter[shardNumber] += numBuckets;
            }

            mapPointersAndSizesArray[shardNumber].setElementValue((long)bitsPerMapFixedLengthPortion * shardOrdinal, bitsPerMapPointer, bucketCounter[shardNumber]);
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

        mapPointersAndSizesArray = null;
        entryData = null;
    }
    
    private void writeSnapshotShard(DataOutputStream os, int shardNumber) throws IOException {
        int bitsPerMapFixedLengthPortion = bitsPerMapSizeValue + bitsPerMapPointer;
        int bitsPerMapEntry = bitsPerKeyElement + bitsPerValueElement;

        /// 1) max ordinal
        VarInt.writeVInt(os, maxShardOrdinal[shardNumber]);

        /// 2) statistics
        VarInt.writeVInt(os, bitsPerMapPointer);
        VarInt.writeVInt(os, bitsPerMapSizeValue);
        VarInt.writeVInt(os, bitsPerKeyElement);
        VarInt.writeVInt(os, bitsPerValueElement);
        VarInt.writeVLong(os, totalOfMapBuckets[shardNumber]);

        /// 3) list pointer array
        int numMapFixedLengthLongs = maxShardOrdinal[shardNumber] == -1 ? 0 : (int)((((long)(maxShardOrdinal[shardNumber] + 1) * bitsPerMapFixedLengthPortion) - 1) / 64) + 1;
        VarInt.writeVInt(os, numMapFixedLengthLongs);
        for(int i=0;i<numMapFixedLengthLongs;i++) {
            os.writeLong(mapPointersAndSizesArray[shardNumber].get(i));
        }

        /// 4) element array
        int numElementLongs = totalOfMapBuckets[shardNumber] == 0 ? 0 : (int)(((totalOfMapBuckets[shardNumber] * bitsPerMapEntry) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(entryData[shardNumber].get(i));
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
        int bitsPerMapFixedLengthPortion = bitsPerMapSizeValue + bitsPerMapPointer;
        int bitsPerMapEntry = bitsPerKeyElement + bitsPerValueElement;

        numMapsInDelta = new int[numShards];
        numBucketsInDelta = new long[numShards];
        mapPointersAndSizesArray = new FixedLengthElementArray[numShards];
        entryData = new FixedLengthElementArray[numShards];
        deltaAddedOrdinals = new ByteDataBuffer[numShards];
        deltaRemovedOrdinals = new ByteDataBuffer[numShards];
        
        ThreadSafeBitSet deltaAdditions = toCyclePopulated.andNot(fromCyclePopulated);
        
        int shardMask = numShards - 1;
        
        int addedOrdinal = deltaAdditions.nextSetBit(0);
        while(addedOrdinal != -1) {
            numMapsInDelta[addedOrdinal & shardMask]++;
            long readPointer = ordinalMap.getPointerForData(addedOrdinal);
            int size = VarInt.readVInt(ordinalMap.getByteData().getUnderlyingArray(), readPointer);
            numBucketsInDelta[addedOrdinal & shardMask] += HashCodes.hashTableSize(size);

            addedOrdinal = deltaAdditions.nextSetBit(addedOrdinal + 1);
        }

        for(int i=0;i<numShards;i++) {
            mapPointersAndSizesArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numMapsInDelta[i] * bitsPerMapFixedLengthPortion);
            entryData[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, numBucketsInDelta[i] * bitsPerMapEntry);
            deltaAddedOrdinals[i] = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
            deltaRemovedOrdinals[i] = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
        }

        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        int mapCounter[] = new int[numShards];
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

                mapPointersAndSizesArray[shardNumber].setElementValue((long)bitsPerMapFixedLengthPortion * mapCounter[shardNumber], bitsPerMapPointer, endBucketPosition);
                mapPointersAndSizesArray[shardNumber].setElementValue(((long)bitsPerMapFixedLengthPortion * mapCounter[shardNumber]) + bitsPerMapPointer, bitsPerMapSizeValue, size);

                int keyElementOrdinal = 0;

                for(int j=0;j<numBuckets;j++) {
                    entryData[shardNumber].setElementValue((long)bitsPerMapEntry * (bucketCounter[shardNumber] + j), bitsPerKeyElement, (1L << bitsPerKeyElement) - 1);
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

                    while(entryData[shardNumber].getElementValue((long)bitsPerMapEntry * (bucketCounter[shardNumber] + hashedBucket), bitsPerKeyElement) != ((1L << bitsPerKeyElement) - 1)) {
                        hashedBucket++;
                        hashedBucket &= (numBuckets - 1);
                    }

                    long mapEntryBitOffset = (long)bitsPerMapEntry * (bucketCounter[shardNumber] + hashedBucket);
                    entryData[shardNumber].clearElementValue(mapEntryBitOffset, bitsPerMapEntry);
                    entryData[shardNumber].setElementValue(mapEntryBitOffset, bitsPerKeyElement, keyElementOrdinal);
                    entryData[shardNumber].setElementValue(mapEntryBitOffset + bitsPerKeyElement, bitsPerValueElement, valueElementOrdinal);
                }

                bucketCounter[shardNumber] += numBuckets;
                mapCounter[shardNumber]++;

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
        
        mapPointersAndSizesArray = null;
        entryData = null;
        deltaAddedOrdinals = null;
        deltaRemovedOrdinals = null;
    }
    
    private void writeCalculatedDeltaShard(DataOutputStream os, int shardNumber) throws IOException {
        
        int bitsPerMapFixedLengthPortion = bitsPerMapSizeValue + bitsPerMapPointer;
        int bitsPerMapEntry = bitsPerKeyElement + bitsPerValueElement;

        /// 1) max ordinal
        VarInt.writeVInt(os, maxShardOrdinal[shardNumber]);

        /// 2) removal / addition ordinals.
        VarInt.writeVLong(os, deltaRemovedOrdinals[shardNumber].length());
        deltaRemovedOrdinals[shardNumber].getUnderlyingArray().writeTo(os, 0, deltaRemovedOrdinals[shardNumber].length());
        VarInt.writeVLong(os, deltaAddedOrdinals[shardNumber].length());
        deltaAddedOrdinals[shardNumber].getUnderlyingArray().writeTo(os, 0, deltaAddedOrdinals[shardNumber].length());

        /// 3) statistics
        VarInt.writeVInt(os, bitsPerMapPointer);
        VarInt.writeVInt(os, bitsPerMapSizeValue);
        VarInt.writeVInt(os, bitsPerKeyElement);
        VarInt.writeVInt(os, bitsPerValueElement);
        VarInt.writeVLong(os, totalOfMapBuckets[shardNumber]);

        /// 4) pointer array
        int numMapFixedLengthLongs = numMapsInDelta[shardNumber] == 0 ? 0 : (int)((((long)numMapsInDelta[shardNumber] * bitsPerMapFixedLengthPortion) - 1) / 64) + 1;
        VarInt.writeVInt(os, numMapFixedLengthLongs);
        for(int i=0;i<numMapFixedLengthLongs;i++) {
            os.writeLong(mapPointersAndSizesArray[shardNumber].get(i));
        }

        /// 5) element array
        int numElementLongs = numBucketsInDelta[shardNumber] == 0 ? 0 : (int)(((numBucketsInDelta[shardNumber] * bitsPerMapEntry) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(entryData[shardNumber].get(i));
        }
    }
}
