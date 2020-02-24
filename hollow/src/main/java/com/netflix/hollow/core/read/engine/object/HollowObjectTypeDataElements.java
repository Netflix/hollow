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
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;

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

    void readSnapshot(RandomAccessFile raf, MappedByteBuffer buffer, BufferedWriter debug, HollowObjectSchema unfilteredSchema) throws IOException {
        readFromStream(raf, buffer, debug, false, unfilteredSchema);
    }

    void readFromStream(RandomAccessFile raf, MappedByteBuffer buffer, BufferedWriter debug, boolean isDelta, HollowObjectSchema unfilteredSchema) throws IOException {
        maxOrdinal = VarInt.readVInt(raf);

        if(isDelta) {
            throw new UnsupportedOperationException("Deltas not supported");
        }

        readFieldStatistics(raf, unfilteredSchema);

        fixedLengthData = FixedLengthElementArray.deserializeFrom(raf, buffer, memoryRecycler);
        // removeExcludedFieldsFromFixedLengthData();
        readVarLengthData(raf, buffer, unfilteredSchema);

        // debug.append("HollowObjectTypeDataElements for " + schema.toString() + " \n");
        // fixedLengthData.pp(debug);
        // for (int i= 0; i < varLengthData.length; i++) {
        //     if (varLengthData[i] != null) {
        //         varLengthData[i].pp(debug);
        //     }
        // }
        // debug.append("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * end HollowObjectTypeDataElements for " + schema.toString() + "\n");
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


    private void readVarLengthData(RandomAccessFile raf, MappedByteBuffer buffer, HollowObjectSchema unfilteredSchema) throws IOException {
        int filteredFieldIdx = 0;

        for(int i=0;i<unfilteredSchema.numFields();i++) {
            long numBytesInVarLengthData = VarInt.readVLong(raf);

            if(schema.getPosition(unfilteredSchema.getFieldName(i)) != -1) {
                if(numBytesInVarLengthData != 0) {
                    varLengthData[filteredFieldIdx] = new SegmentedByteArray(memoryRecycler);
                    varLengthData[filteredFieldIdx].readFrom(raf, buffer, numBytesInVarLengthData);
                }
                filteredFieldIdx++;
            } else {
                throw new UnsupportedOperationException("Filtering is not yet supported");
            }
        }
    }

    static void discardFromStream(DataInputStream dis, HollowObjectSchema schema, int numShards, boolean isDelta) throws IOException {
        throw new UnsupportedOperationException();
    }

    void applyDelta(HollowObjectTypeDataElements fromData, HollowObjectTypeDataElements deltaData) {
        throw new UnsupportedOperationException();
    }

    public void destroy() {
        throw new UnsupportedOperationException();
    }

}
