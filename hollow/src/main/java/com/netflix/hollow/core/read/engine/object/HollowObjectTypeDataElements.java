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
package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class holds the data for a {@link HollowObjectTypeReadState}.
 * 
 * During a delta, the HollowObjectTypeReadState will create a new HollowObjectTypeDataElements and atomically swap
 * with the existing one to make sure a consistent view of the data is always available. 
 */
public class HollowObjectTypeDataElements {

    final HollowObjectSchema schema;

    int maxOrdinal;

    FixedLengthElementArray fixedLengthData;
    final SegmentedByteArray varLengthData[];

    GapEncodedVariableLengthIntegerReader encodedAdditions;
    GapEncodedVariableLengthIntegerReader encodedRemovals;

    final int bitsPerField[];
    final int bitOffsetPerField[];
    final long nullValueForField[];
    int bitsPerRecord;

    private int bitsPerUnfilteredField[];
    private boolean unfilteredFieldIsIncluded[];

    final ArraySegmentRecycler memoryRecycler;

    public HollowObjectTypeDataElements(HollowObjectSchema schema, ArraySegmentRecycler memoryRecycler) {
        varLengthData = new SegmentedByteArray[schema.numFields()];
        bitsPerField = new int[schema.numFields()];
        bitOffsetPerField = new int[schema.numFields()];
        nullValueForField = new long[schema.numFields()];
        this.schema = schema;
        this.memoryRecycler = memoryRecycler;
    }

    void readSnapshot(RandomAccessFile raf, HollowObjectSchema unfilteredSchema) throws IOException {
        readFromStream(raf, false, unfilteredSchema);
    }

    void readDelta(RandomAccessFile raf) throws IOException {
        // readFromStream(raf, true, schema);
        throw new UnsupportedOperationException();
    }

    void readFromStream(RandomAccessFile raf, boolean isDelta, HollowObjectSchema unfilteredSchema) throws IOException {
        maxOrdinal = VarInt.readVInt(raf);

        if(isDelta) {
            throw new UnsupportedOperationException("Deltas not supported");
        //    encodedRemovals = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(dis, memoryRecycler);
        //    encodedAdditions = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(dis, memoryRecycler);
        }

        readFieldStatistics(raf, unfilteredSchema);

        fixedLengthData = FixedLengthElementArray.deserializeFrom(raf, memoryRecycler);
        // removeExcludedFieldsFromFixedLengthData();

        // SNAP: Everything until here is at parity
        readVarLengthData(raf, unfilteredSchema);
    }

    private void removeExcludedFieldsFromFixedLengthData() {
        if(bitsPerField.length < bitsPerUnfilteredField.length) {
            long numBitsRequired = (long)bitsPerRecord * (maxOrdinal + 1);
            FixedLengthElementArray filteredData = new FixedLengthElementArray(memoryRecycler, numBitsRequired);

            long currentReadBit = 0;
            long currentWriteBit = 0;

            for(int i=0;i<=maxOrdinal;i++) {
                for(int j=0;j<bitsPerUnfilteredField.length;j++) {
                    if(unfilteredFieldIsIncluded[j]) {
                        long value = bitsPerUnfilteredField[j] < 56 ?
                                fixedLengthData.getElementValue(currentReadBit, bitsPerUnfilteredField[j]) :
                                    fixedLengthData.getLargeElementValue(currentReadBit, bitsPerUnfilteredField[j]);
                        filteredData.setElementValue(currentWriteBit, bitsPerUnfilteredField[j], value);
                        currentWriteBit += bitsPerUnfilteredField[j];
                    }

                    currentReadBit += bitsPerUnfilteredField[j];
                }
            }

            fixedLengthData.destroy(memoryRecycler);
            memoryRecycler.swap();
            fixedLengthData = filteredData;
        }
    }

    private void readFieldStatistics(RandomAccessFile raf, HollowObjectSchema unfilteredSchema) throws IOException {
        bitsPerRecord = 0;

        bitsPerUnfilteredField = new int[unfilteredSchema.numFields()];
        unfilteredFieldIsIncluded = new boolean[unfilteredSchema.numFields()];

        int filteredFieldIdx = 0;

        for(int i=0;i<unfilteredSchema.numFields();i++) {
            int readBitsPerField = VarInt.readVInt(raf);
            bitsPerUnfilteredField[i] = readBitsPerField;
            unfilteredFieldIsIncluded[i] = schema.getPosition(unfilteredSchema.getFieldName(i)) != -1;

            if(unfilteredFieldIsIncluded[i]) {
                bitsPerField[filteredFieldIdx] = readBitsPerField;
                nullValueForField[filteredFieldIdx] = (1L << bitsPerField[filteredFieldIdx]) - 1;
                bitOffsetPerField[filteredFieldIdx] = bitsPerRecord;
                bitsPerRecord += bitsPerField[filteredFieldIdx];
                filteredFieldIdx++;
            }
        }
    }


    private void readVarLengthData(RandomAccessFile raf, HollowObjectSchema unfilteredSchema) throws IOException {
        int filteredFieldIdx = 0;

        for(int i=0;i<unfilteredSchema.numFields();i++) {
            long numBytesInVarLengthData = VarInt.readVLong(raf);

            if(schema.getPosition(unfilteredSchema.getFieldName(i)) != -1) {
                if(numBytesInVarLengthData != 0) {
                    varLengthData[filteredFieldIdx] = new SegmentedByteArray(memoryRecycler);
                    varLengthData[filteredFieldIdx].readFrom(raf, numBytesInVarLengthData);

                    varLengthData[filteredFieldIdx] = new SegmentedByteArray(memoryRecycler);
                    varLengthData[filteredFieldIdx].readFrom(raf, numBytesInVarLengthData);
                }
                filteredFieldIdx++;
            } else {
                while(numBytesInVarLengthData > 0) {
                    numBytesInVarLengthData -= raf.skipBytes((int) numBytesInVarLengthData);
                }
            }
        }
    }

    static void discardFromStream(DataInputStream dis, HollowObjectSchema schema, int numShards, boolean isDelta) throws IOException {
        if(numShards > 1)
            VarInt.readVInt(dis); // max ordinal
        
        for(int i=0;i<numShards;i++) {
            VarInt.readVInt(dis); // max ordinal

            if(isDelta) {
                /// addition/removal ordinals
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(dis);
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(dis);
            }
    
            /// field statistics
            for(int j=0;j<schema.numFields();j++) {
                VarInt.readVInt(dis);
            }
    
            /// fixed length data
            FixedLengthElementArray.discardFrom(dis);
    
            /// variable length data
            for(int j=0;j<schema.numFields();j++) {
                long numBytesInVarLengthData = VarInt.readVLong(dis);
                while(numBytesInVarLengthData > 0) {
                    numBytesInVarLengthData -= dis.skip(numBytesInVarLengthData);
                }
            }
        }
    }

    void applyDelta(HollowObjectTypeDataElements fromData, HollowObjectTypeDataElements deltaData) {
        throw new UnsupportedOperationException();
        // new HollowObjectDeltaApplicatortaApplicator(fromData, deltaData, this).applyDelta();
    }

    public void destroy() {
        fixedLengthData.destroy(memoryRecycler);
        for(int i=0;i<varLengthData.length;i++) {
            if(varLengthData[i] != null)
                varLengthData[i].destroy();
        }
    }

}
