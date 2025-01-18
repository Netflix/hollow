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
import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.schema.HollowListSchema;
import java.io.DataOutputStream;
import java.io.IOException;

public class HollowListTypeWriteState extends HollowTypeWriteState {

    /// statistics required for writing fixed length list data
    private int bitsPerListPointer;
    private int revBitsPerListPointer;
    private int bitsPerElement;
    private long totalOfListSizes[];
    private long revTotalOfListSizes[];

    /// data required for writing snapshot or delta
    private FixedLengthElementArray listPointerArray[];
    private FixedLengthElementArray elementArray[];

    /// additional data required for writing delta
    private int numListsInDelta[];
    private long numElementsInDelta[];
    private ByteDataArray deltaAddedOrdinals[];
    private ByteDataArray deltaRemovedOrdinals[];

    public HollowListTypeWriteState(HollowListSchema schema) {
        this(schema, -1);
    }
    
    public HollowListTypeWriteState(HollowListSchema schema, int numShards) {
        this(schema, numShards, false);
    }

    public HollowListTypeWriteState(HollowListSchema schema, int numShards, boolean isNumShardsPinned) {
        super(schema, numShards, isNumShardsPinned);
    }

    @Override
    public HollowListSchema getSchema() {
        return (HollowListSchema)schema;
    }

    @Override
    public void prepareForWrite() {
        super.prepareForWrite();

        maxOrdinal = ordinalMap.maxOrdinal();
        gatherShardingStats(maxOrdinal);
        gatherStatistics(numShards != revNumShards);
    }

    private void gatherStatistics(boolean numShardsChanged) {

        int maxElementOrdinal = 0;
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        totalOfListSizes = new long[numShards];
        if (numShardsChanged) {
            revTotalOfListSizes = new long[revNumShards];
        }

        for(int i=0;i<=maxOrdinal;i++) {
            if(currentCyclePopulated.get(i) || previousCyclePopulated.get(i)) {
                long pointer = ordinalMap.getPointerForData(i);
                int size = VarInt.readVInt(data, pointer);

                pointer += VarInt.sizeOfVInt(size);

                for(int j=0;j<size;j++) {
                    int elementOrdinal = VarInt.readVInt(data, pointer);
                    if(elementOrdinal > maxElementOrdinal)
                        maxElementOrdinal = elementOrdinal;
                    pointer += VarInt.sizeOfVInt(elementOrdinal);
                }

                totalOfListSizes[i & (numShards-1)] += size;
                if (numShardsChanged) {
                    revTotalOfListSizes[i & (revNumShards-1)] += size;
                }
            }
        }
        
        long maxShardTotalOfListSizes = 0;
        for(int i=0;i<numShards;i++) {
            if(totalOfListSizes[i] > maxShardTotalOfListSizes)
                maxShardTotalOfListSizes = totalOfListSizes[i];
        }
        bitsPerElement = maxElementOrdinal == 0 ? 1 : 64 - Long.numberOfLeadingZeros(maxElementOrdinal);
        bitsPerListPointer = maxShardTotalOfListSizes == 0 ? 1 : 64 - Long.numberOfLeadingZeros(maxShardTotalOfListSizes);

        if (numShardsChanged) {
            long revMaxShardTotalOfListSizes = 0;
            for(int i=0;i<revNumShards;i++) {
                if(revTotalOfListSizes[i] > revMaxShardTotalOfListSizes)
                    revMaxShardTotalOfListSizes = revTotalOfListSizes[i];
            }
            revBitsPerListPointer = revMaxShardTotalOfListSizes == 0 ? 1 : 64 - Long.numberOfLeadingZeros(revMaxShardTotalOfListSizes);
        }
    }

    @Override
    protected int typeStateNumShards(int maxOrdinal) {
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();
        
        long maxElementOrdinal = 0;
        long totalOfListSizes = 0;
        
        for(int i=0;i<=maxOrdinal;i++) {
            if(currentCyclePopulated.get(i) || previousCyclePopulated.get(i)) {
                long pointer = ordinalMap.getPointerForData(i);
                int size = VarInt.readVInt(data, pointer);

                pointer += VarInt.sizeOfVInt(size);

                for(int j=0;j<size;j++) {
                    int elementOrdinal = VarInt.readVInt(data, pointer);
                    if(elementOrdinal > maxElementOrdinal)
                        maxElementOrdinal = elementOrdinal;
                    pointer += VarInt.sizeOfVInt(elementOrdinal);
                }

                totalOfListSizes += size;
            }
        }

        long bitsPerElement = maxElementOrdinal == 0 ? 1 : 64 - Long.numberOfLeadingZeros(maxElementOrdinal);
        long bitsPerListPointer = totalOfListSizes == 0 ? 1 : 64 - Long.numberOfLeadingZeros(totalOfListSizes);
        
        long projectedSizeOfType = (bitsPerElement * totalOfListSizes) / 8;
        projectedSizeOfType += (bitsPerListPointer * maxOrdinal + 1) / 8;
        
        int targetNumShards = 1;
        while(stateEngine.getTargetMaxTypeShardSize() * targetNumShards < projectedSizeOfType)
            targetNumShards *= 2;
        return targetNumShards;
    }
    
    @Override
    public void calculateSnapshot() {
        listPointerArray = new FixedLengthElementArray[numShards];
        elementArray = new FixedLengthElementArray[numShards];

        for(int i=0;i<numShards;i++) {
            listPointerArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerListPointer * (maxShardOrdinal[i] + 1));
            elementArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerElement * totalOfListSizes[i]);
        }

        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        long elementCounter[] = new long[numShards];
        int shardMask = numShards - 1;

        for(int ordinal=0;ordinal<=maxOrdinal;ordinal++) {
            int shardNumber = ordinal & shardMask;
            int shardOrdinal = ordinal / numShards;
            
            if(currentCyclePopulated.get(ordinal)) {
                long readPointer = ordinalMap.getPointerForData(ordinal);

                int size = VarInt.readVInt(data, readPointer);
                readPointer += VarInt.sizeOfVInt(size);

                for(int j=0;j<size;j++) {
                    int elementOrdinal = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(elementOrdinal);
                    elementArray[shardNumber].setElementValue((long)bitsPerElement * elementCounter[shardNumber], bitsPerElement, elementOrdinal);
                    elementCounter[shardNumber]++;
                }
            }

            listPointerArray[shardNumber].setElementValue((long)bitsPerListPointer * shardOrdinal, bitsPerListPointer, elementCounter[shardNumber]);
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

        listPointerArray = null;
        elementArray = null;
    }
        
    private void writeSnapshotShard(DataOutputStream os, int shardNumber) throws IOException {
        /// 1) shard max ordinal
        VarInt.writeVInt(os, maxShardOrdinal[shardNumber]);

        /// 2) statistics
        VarInt.writeVInt(os, bitsPerListPointer);
        VarInt.writeVInt(os, bitsPerElement);
        VarInt.writeVLong(os, totalOfListSizes[shardNumber]);

        /// 3) list pointer array
        int numListPointerLongs = maxShardOrdinal[shardNumber] == -1 ? 0 : (int)((((long)(maxShardOrdinal[shardNumber] + 1) * bitsPerListPointer) - 1) / 64) + 1;
        VarInt.writeVInt(os, numListPointerLongs);
        for(int i=0;i<numListPointerLongs;i++) {
            os.writeLong(listPointerArray[shardNumber].get(i));
        }

        /// 4) element array
        int numElementLongs = totalOfListSizes[shardNumber] == 0 ? 0 : (int)(((totalOfListSizes[shardNumber] * bitsPerElement) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(elementArray[shardNumber].get(i));
        }
    }

    @Override
    public void calculateDelta(ThreadSafeBitSet fromCyclePopulated, ThreadSafeBitSet toCyclePopulated, boolean isReverse) {
        int numShards = this.numShards;
        int bitsPerListPointer = this.bitsPerListPointer;
        if (isReverse && this.numShards != this.revNumShards) {
            numShards = this.revNumShards;
            bitsPerListPointer = this.revBitsPerListPointer;
        }

        numListsInDelta = new int[numShards];
        numElementsInDelta = new long[numShards];
        
        listPointerArray = new FixedLengthElementArray[numShards];
        elementArray = new FixedLengthElementArray[numShards];
        deltaAddedOrdinals = new ByteDataArray[numShards];
        deltaRemovedOrdinals = new ByteDataArray[numShards];
        
        ThreadSafeBitSet deltaAdditions = toCyclePopulated.andNot(fromCyclePopulated);
        
        int shardMask = numShards - 1;
        
        int addedOrdinal = deltaAdditions.nextSetBit(0);
        while(addedOrdinal != -1) {
            numListsInDelta[addedOrdinal & shardMask]++;
            long readPointer = ordinalMap.getPointerForData(addedOrdinal);
            numElementsInDelta[addedOrdinal & shardMask] += VarInt.readVInt(ordinalMap.getByteData().getUnderlyingArray(), readPointer);
            
            addedOrdinal = deltaAdditions.nextSetBit(addedOrdinal + 1);
        }
        
        for(int i=0;i<numShards;i++) {
            listPointerArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numListsInDelta[i] * bitsPerListPointer);
            elementArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, numElementsInDelta[i] * bitsPerElement);
            deltaAddedOrdinals[i] = new ByteDataArray(WastefulRecycler.DEFAULT_INSTANCE);
            deltaRemovedOrdinals[i] = new ByteDataArray(WastefulRecycler.DEFAULT_INSTANCE);
        }

        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        int listCounter[] = new int[numShards];
        long elementCounter[] = new long[numShards];
        int previousRemovedOrdinal[] = new int[numShards];
        int previousAddedOrdinal[] = new int[numShards];

        for(int ordinal=0;ordinal<=maxOrdinal;ordinal++) {
            int shardNumber = ordinal & shardMask;
            if(deltaAdditions.get(ordinal)) {
                long readPointer = ordinalMap.getPointerForData(ordinal);

                int size = VarInt.readVInt(data, readPointer);
                readPointer += VarInt.sizeOfVInt(size);

                listPointerArray[shardNumber].setElementValue((long)bitsPerListPointer * listCounter[shardNumber], bitsPerListPointer, elementCounter[shardNumber] + size);

                for(int j=0;j<size;j++) {
                    int elementOrdinal = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(elementOrdinal);
                    elementArray[shardNumber].setElementValue((long)bitsPerElement * elementCounter[shardNumber], bitsPerElement, elementOrdinal);
                    elementCounter[shardNumber]++;
                }

                listCounter[shardNumber]++;

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
    public void writeCalculatedDelta(DataOutputStream os, boolean isReverse, int[] maxShardOrdinal) throws IOException {
        int numShards = this.numShards;
        int bitsPerListPointer = this.bitsPerListPointer;
        long[] totalOfListSizes = this.totalOfListSizes;
        if (isReverse && this.numShards != this.revNumShards) {
            numShards = this.revNumShards;
            bitsPerListPointer = this.revBitsPerListPointer;
            totalOfListSizes = this.revTotalOfListSizes;
        }


        /// for unsharded blobs, support pre v2.1.0 clients
        if(numShards == 1) {
            writeCalculatedDeltaShard(os, 0, maxShardOrdinal, bitsPerListPointer, totalOfListSizes);
        } else {
            /// overall max ordinal
            VarInt.writeVInt(os, maxOrdinal);
            
            for(int i=0;i<numShards;i++) {
                writeCalculatedDeltaShard(os, i, maxShardOrdinal, bitsPerListPointer, totalOfListSizes);
            }
        }

        listPointerArray = null;
        elementArray = null;
        deltaAddedOrdinals = null;
        deltaRemovedOrdinals = null;
    }


    private void writeCalculatedDeltaShard(DataOutputStream os, int shardNumber, int[] maxShardOrdinal, int bitsPerListPointer, long[] totalOfListSizes) throws IOException {
        /// 1) max shard ordinal
        VarInt.writeVInt(os, maxShardOrdinal[shardNumber]);

        /// 2) removal / addition ordinals.
        VarInt.writeVLong(os, deltaRemovedOrdinals[shardNumber].length());
        deltaRemovedOrdinals[shardNumber].getUnderlyingArray().writeTo(os, 0, deltaRemovedOrdinals[shardNumber].length());
        VarInt.writeVLong(os, deltaAddedOrdinals[shardNumber].length());
        deltaAddedOrdinals[shardNumber].getUnderlyingArray().writeTo(os, 0, deltaAddedOrdinals[shardNumber].length());

        /// 3) statistics
        VarInt.writeVInt(os, bitsPerListPointer);
        VarInt.writeVInt(os, bitsPerElement);
        VarInt.writeVLong(os, totalOfListSizes[shardNumber]);

        /// 4) list pointer array
        int numListPointerLongs = numListsInDelta[shardNumber] == 0 ? 0 : (int)((((long)numListsInDelta[shardNumber] * bitsPerListPointer) - 1) / 64) + 1;
        VarInt.writeVInt(os, numListPointerLongs);
        for(int i=0;i<numListPointerLongs;i++) {
            os.writeLong(listPointerArray[shardNumber].get(i));
        }

        /// 5) element array
        int numElementLongs = numElementsInDelta[shardNumber] == 0 ? 0 : (int)(((numElementsInDelta[shardNumber] * bitsPerElement) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(elementArray[shardNumber].get(i));
        }
    }
}
