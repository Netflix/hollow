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
import com.netflix.hollow.core.memory.encoding.VarInt;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.io.DataOutputStream;
import java.io.IOException;

public class HollowListTypeWriteState extends HollowTypeWriteState {

    /// statistics required for writing fixed length list data
    private int bitsPerListPointer;
    private int bitsPerElement;
    private long totalOfListSizes;

    /// data required for writing snapshot or delta
    private int maxOrdinal;
    private FixedLengthElementArray listPointerArray;
    private FixedLengthElementArray elementArray;

    /// additional data required for writing delta
    private int numListsInDelta;
    private long numElementsInDelta;
    private ByteDataBuffer deltaAddedOrdinals;
    private ByteDataBuffer deltaRemovedOrdinals;

    public HollowListTypeWriteState(HollowListSchema schema) {
        super(schema, 1);
    }

    @Override
    public HollowListSchema getSchema() {
        return (HollowListSchema)schema;
    }

    @Override
    public void prepareForWrite() {
        super.prepareForWrite();

        totalOfListSizes = 0;

        gatherStatistics();
    }

    private void gatherStatistics() {
        int maxElementOrdinal = 0;

        int maxOrdinal = ordinalMap.maxOrdinal();
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

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

        bitsPerElement = maxElementOrdinal == 0 ? 1 : 64 - Long.numberOfLeadingZeros(maxElementOrdinal);

        bitsPerListPointer = totalOfListSizes == 0 ? 1 : 64 - Long.numberOfLeadingZeros(totalOfListSizes);
    }

    @Override
    public void calculateSnapshot() {
        maxOrdinal = ordinalMap.maxOrdinal();

        listPointerArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerListPointer * (maxOrdinal + 1));
        elementArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)bitsPerElement * totalOfListSizes);

        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        int elementCounter = 0;

        for(int i=0;i<=maxOrdinal;i++) {
            if(currentCyclePopulated.get(i)) {
                long readPointer = ordinalMap.getPointerForData(i);

                int size = VarInt.readVInt(data, readPointer);
                readPointer += VarInt.sizeOfVInt(size);

                for(int j=0;j<size;j++) {
                    int elementOrdinal = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(elementOrdinal);
                    elementArray.setElementValue((long)bitsPerElement * elementCounter, bitsPerElement, elementOrdinal);
                    elementCounter++;
                }
            }

            listPointerArray.setElementValue((long)bitsPerListPointer * i, bitsPerListPointer, elementCounter);
        }
    }

    @Override
    public void writeSnapshot(DataOutputStream os) throws IOException {
        /// 1) max ordinal
        VarInt.writeVInt(os, maxOrdinal);

        /// 2) statistics
        VarInt.writeVInt(os, bitsPerListPointer);
        VarInt.writeVInt(os, bitsPerElement);
        VarInt.writeVLong(os, totalOfListSizes);

        /// 3) list pointer array
        int numListPointerLongs = maxOrdinal == -1 ? 0 : (int)((((long)(maxOrdinal + 1) * bitsPerListPointer) - 1) / 64) + 1;
        VarInt.writeVInt(os, numListPointerLongs);
        for(int i=0;i<numListPointerLongs;i++) {
            os.writeLong(listPointerArray.get(i));
        }

        /// 4) element array
        int numElementLongs = totalOfListSizes == 0 ? 0 : (int)(((totalOfListSizes * bitsPerElement) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(elementArray.get(i));
        }

        /// 5) Populated bits
        currentCyclePopulated.serializeBitsTo(os);

        listPointerArray = null;
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

    private void calculateDelta(ThreadSafeBitSet fromCyclePopulated, ThreadSafeBitSet toCyclePopulated) {
        maxOrdinal = ordinalMap.maxOrdinal();

        ThreadSafeBitSet deltaAdditions = toCyclePopulated.andNot(fromCyclePopulated);
        numListsInDelta = deltaAdditions.cardinality();
        numElementsInDelta = calculateNumElementsInDelta(maxOrdinal, deltaAdditions);

        listPointerArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numListsInDelta * bitsPerListPointer);
        elementArray = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, numElementsInDelta * bitsPerElement);
        deltaAddedOrdinals = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
        deltaRemovedOrdinals = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);

        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        int listCounter = 0;
        int elementCounter = 0;

        int previousRemovedOrdinal = 0;
        int previousAddedOrdinal = 0;


        for(int i=0;i<=maxOrdinal;i++) {
            if(toCyclePopulated.get(i) && !fromCyclePopulated.get(i)) {
                long readPointer = ordinalMap.getPointerForData(i);

                int size = VarInt.readVInt(data, readPointer);
                readPointer += VarInt.sizeOfVInt(size);

                listPointerArray.setElementValue((long)bitsPerListPointer * listCounter, bitsPerListPointer, elementCounter + size);

                for(int j=0;j<size;j++) {
                    int elementOrdinal = VarInt.readVInt(data, readPointer);
                    readPointer += VarInt.sizeOfVInt(elementOrdinal);
                    elementArray.setElementValue((long)bitsPerElement * elementCounter, bitsPerElement, elementOrdinal);
                    elementCounter++;
                }

                listCounter++;

                VarInt.writeVInt(deltaAddedOrdinals, i - previousAddedOrdinal);
                previousAddedOrdinal = i;
            } else if(fromCyclePopulated.get(i) && !toCyclePopulated.get(i)) {
                VarInt.writeVInt(deltaRemovedOrdinals, i - previousRemovedOrdinal);
                previousRemovedOrdinal = i;
            }
        }
    }

    public void writeCalculatedDelta(DataOutputStream os) throws IOException {

        /// 1) max ordinal
        VarInt.writeVInt(os, maxOrdinal);

        /// 2) removal / addition ordinals.
        VarInt.writeVLong(os, deltaRemovedOrdinals.length());
        deltaRemovedOrdinals.getUnderlyingArray().writeTo(os, 0, deltaRemovedOrdinals.length());
        VarInt.writeVLong(os, deltaAddedOrdinals.length());
        deltaAddedOrdinals.getUnderlyingArray().writeTo(os, 0, deltaAddedOrdinals.length());

        /// 3) statistics
        VarInt.writeVInt(os, bitsPerListPointer);
        VarInt.writeVInt(os, bitsPerElement);
        VarInt.writeVLong(os, totalOfListSizes);

        /// 4) list pointer array
        int numListPointerLongs = numListsInDelta == 0 ? 0 : (int)((((long)numListsInDelta * bitsPerListPointer) - 1) / 64) + 1;
        VarInt.writeVInt(os, numListPointerLongs);
        for(int i=0;i<numListPointerLongs;i++) {
            os.writeLong(listPointerArray.get(i));
        }

        /// 5) element array
        int numElementLongs = numElementsInDelta == 0 ? 0 : (int)(((numElementsInDelta * bitsPerElement) - 1) / 64) + 1;
        VarInt.writeVInt(os, numElementLongs);
        for(int i=0;i<numElementLongs;i++) {
            os.writeLong(elementArray.get(i));
        }

        listPointerArray = null;
        elementArray = null;
        deltaAddedOrdinals = null;
        deltaRemovedOrdinals = null;
    }

    private long calculateNumElementsInDelta(int maxOrdinal, ThreadSafeBitSet deltaAdditions) {
        long totalNumberOfElements = 0;

        for(int i=0;i<=maxOrdinal;i++) {
            if(deltaAdditions.get(i)) {
                long readPointer = ordinalMap.getPointerForData(i);
                totalNumberOfElements += VarInt.readVInt(ordinalMap.getByteData().getUnderlyingArray(), readPointer);
            }
        }

        return totalNumberOfElements;
    }

}
