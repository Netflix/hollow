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
package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.VariableLengthData;
import com.netflix.hollow.core.memory.VariableLengthDataFactory;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import java.io.IOException;

/**
 * This class holds the data for a {@link HollowObjectTypeReadState}.
 * 
 * During a delta, the HollowObjectTypeReadState will create a new HollowObjectTypeDataElements and atomically swap
 * with the existing one to make sure a consistent view of the data is always available. 
 */
public class HollowObjectTypeDataElements {

    final HollowObjectSchema schema;

    int maxOrdinal;

    FixedLengthData fixedLengthData;
    final VariableLengthData varLengthData[];

    GapEncodedVariableLengthIntegerReader encodedAdditions;
    GapEncodedVariableLengthIntegerReader encodedRemovals;

    final int bitsPerField[];
    final int bitOffsetPerField[];
    final long nullValueForField[];
    int bitsPerRecord;

    private int bitsPerUnfilteredField[];
    private boolean unfilteredFieldIsIncluded[];

    final ArraySegmentRecycler memoryRecycler;
    final MemoryMode memoryMode;

    public HollowObjectTypeDataElements(HollowObjectSchema schema, ArraySegmentRecycler memoryRecycler) {
        this(schema, MemoryMode.ON_HEAP, memoryRecycler);
    }

    public HollowObjectTypeDataElements(HollowObjectSchema schema, MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        varLengthData = new VariableLengthData[schema.numFields()];
        bitsPerField = new int[schema.numFields()];
        bitOffsetPerField = new int[schema.numFields()];
        nullValueForField = new long[schema.numFields()];
        this.schema = schema;
        this.memoryMode = memoryMode;
        this.memoryRecycler = memoryRecycler;
    }

    void readSnapshot(HollowBlobInput in, HollowObjectSchema unfilteredSchema) throws IOException {
        readFromInput(in, false, unfilteredSchema);
    }

    void readDelta(HollowBlobInput in) throws IOException {
        readFromInput(in, true, schema);
    }

    void readFromInput(HollowBlobInput in, boolean isDelta, HollowObjectSchema unfilteredSchema) throws IOException {
        maxOrdinal = VarInt.readVInt(in);

        if(isDelta) {
            encodedRemovals = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(in, memoryRecycler);
            encodedAdditions = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(in, memoryRecycler);
        }

        readFieldStatistics(in, unfilteredSchema);

        fixedLengthData = FixedLengthDataFactory.get(in, memoryMode, memoryRecycler);
        removeExcludedFieldsFromFixedLengthData();

        readVarLengthData(in, unfilteredSchema);
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

            FixedLengthDataFactory.destroy(fixedLengthData, memoryRecycler);
            memoryRecycler.swap();
            fixedLengthData = filteredData;
        }
    }

    private void readFieldStatistics(HollowBlobInput in, HollowObjectSchema unfilteredSchema) throws IOException {
        bitsPerRecord = 0;

        bitsPerUnfilteredField = new int[unfilteredSchema.numFields()];
        unfilteredFieldIsIncluded = new boolean[unfilteredSchema.numFields()];

        int filteredFieldIdx = 0;

        for(int i=0;i<unfilteredSchema.numFields();i++) {
            int readBitsPerField = VarInt.readVInt(in);
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


    private void readVarLengthData(HollowBlobInput in, HollowObjectSchema unfilteredSchema) throws IOException {
        int filteredFieldIdx = 0;

        for(int i=0;i<unfilteredSchema.numFields();i++) {
            long numBytesInVarLengthData = VarInt.readVLong(in);

            if(schema.getPosition(unfilteredSchema.getFieldName(i)) != -1) {
                if(numBytesInVarLengthData != 0) {
                    varLengthData[filteredFieldIdx] = VariableLengthDataFactory.get(memoryMode, memoryRecycler);
                    varLengthData[filteredFieldIdx].loadFrom(in, numBytesInVarLengthData);
                }
                filteredFieldIdx++;
            } else {
                while(numBytesInVarLengthData > 0) {
                    numBytesInVarLengthData -= in.skipBytes(numBytesInVarLengthData);
                }
            }
        }
    }

    static void discardFromInput(HollowBlobInput in, HollowObjectSchema schema, int numShards, boolean isDelta) throws IOException {
        if(numShards > 1)
            VarInt.readVInt(in); // max ordinal

        for(int i=0;i<numShards;i++) {
            VarInt.readVInt(in); // max ordinal

            if(isDelta) {
                /// addition/removal ordinals
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
                GapEncodedVariableLengthIntegerReader.discardEncodedDeltaOrdinals(in);
            }

            /// field statistics
            for(int j=0;j<schema.numFields();j++) {
                VarInt.readVInt(in);
            }

            /// fixed length data
            FixedLengthData.discardFrom(in);

            /// variable length data
            for(int j=0;j<schema.numFields();j++) {
                long numBytesInVarLengthData = VarInt.readVLong(in);
                while(numBytesInVarLengthData > 0) {
                    numBytesInVarLengthData -= in.skipBytes(numBytesInVarLengthData);
                }
            }
        }
    }

    void applyDelta(HollowObjectTypeDataElements fromData, HollowObjectTypeDataElements deltaData) {
        new HollowObjectDeltaApplicator(fromData, deltaData, this).applyDelta();
    }

    public void destroy() {
        FixedLengthDataFactory.destroy(fixedLengthData, memoryRecycler);
        for(int i=0;i<varLengthData.length;i++) {
            if(varLengthData[i] != null)
                VariableLengthDataFactory.destroy(varLengthData[i]);
        }
    }

}
