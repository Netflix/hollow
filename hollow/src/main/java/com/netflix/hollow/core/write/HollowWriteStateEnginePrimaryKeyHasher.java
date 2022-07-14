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

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.ByteArrayOrdinalMap;
import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;
import com.netflix.hollow.core.schema.HollowObjectSchema;

class HollowWriteStateEnginePrimaryKeyHasher {

    private final HollowObjectTypeWriteState typeStates[][];
    private final int[][] fieldPathIndexes;

    public HollowWriteStateEnginePrimaryKeyHasher(PrimaryKey primaryKey, HollowWriteStateEngine writeEngine) {
        HollowWriteStateEngine stateEngine = writeEngine;
        HollowObjectTypeWriteState rootTypeWriteState = (HollowObjectTypeWriteState) writeEngine.getTypeState(primaryKey.getType());

        this.fieldPathIndexes = new int[primaryKey.numFields()][];
        this.typeStates = new HollowObjectTypeWriteState[primaryKey.numFields()][];

        for(int i = 0; i < primaryKey.numFields(); i++) {
            fieldPathIndexes[i] = primaryKey.getFieldPathIndex(stateEngine, i);
            typeStates[i] = new HollowObjectTypeWriteState[fieldPathIndexes[i].length];

            typeStates[i][0] = rootTypeWriteState;

            for(int j = 1; j < typeStates[i].length; j++) {
                String referencedType = typeStates[i][j - 1].getSchema().getReferencedType(fieldPathIndexes[i][j - 1]);
                typeStates[i][j] = (HollowObjectTypeWriteState) stateEngine.getTypeState(referencedType);
            }
        }
    }

    public int getRecordHash(int ordinal) {
        int hash = 0;

        for(int i = 0; i < fieldPathIndexes.length; i++) {
            hash *= 31;
            hash ^= hashValue(ordinal, i);
        }

        return hash;
    }

    private int hashValue(int ordinal, int fieldIdx) {
        int lastFieldPath = fieldPathIndexes[fieldIdx].length - 1;
        for(int i = 0; i < lastFieldPath; i++) {
            int fieldPosition = fieldPathIndexes[fieldIdx][i];
            ByteArrayOrdinalMap ordinalMap = typeStates[fieldIdx][i].ordinalMap;
            long offset = ordinalMap.getPointerForData(ordinal);
            SegmentedByteArray recordDataArray = ordinalMap.getByteData().getUnderlyingArray();

            offset = navigateToField(typeStates[fieldIdx][i].getSchema(), fieldPosition, recordDataArray, offset);
            ordinal = VarInt.readVInt(recordDataArray, offset);
        }

        int fieldPosition = fieldPathIndexes[fieldIdx][lastFieldPath];
        ByteArrayOrdinalMap ordinalMap = typeStates[fieldIdx][lastFieldPath].ordinalMap;
        long offset = ordinalMap.getPointerForData(ordinal);
        SegmentedByteArray recordDataArray = ordinalMap.getByteData().getUnderlyingArray();
        HollowObjectSchema schema = typeStates[fieldIdx][lastFieldPath].getSchema();

        offset = navigateToField(schema, fieldPosition, recordDataArray, offset);
        return HashCodes.hashInt(fieldHashCode(schema, fieldPosition, recordDataArray, offset));
    }

    private long navigateToField(HollowObjectSchema schema, int fieldIdx, SegmentedByteArray data, long offset) {
        for(int i = 0; i < fieldIdx; i++) {
            switch(schema.getFieldType(i)) {
                case INT:
                case LONG:
                case REFERENCE:
                    offset += VarInt.nextVLongSize(data, offset);
                    break;
                case BYTES:
                case STRING:
                    int fieldLength = VarInt.readVInt(data, offset);
                    offset += VarInt.sizeOfVInt(fieldLength);
                    offset += fieldLength;
                    break;
                case BOOLEAN:
                    offset++;
                    break;
                case DOUBLE:
                    offset += 8;
                    break;
                case FLOAT:
                    offset += 4;
                    break;
            }
        }

        return offset;
    }

    private int fieldHashCode(HollowObjectSchema schema, int fieldIdx, SegmentedByteArray data, long offset) {
        switch(schema.getFieldType(fieldIdx)) {
            case INT:
                if(VarInt.readVNull(data, offset))
                    return 0;
                int intVal = VarInt.readVInt(data, offset);
                intVal = ZigZag.decodeInt(intVal);
                return intVal;
            case LONG:
                if(VarInt.readVNull(data, offset))
                    return 0;
                long longVal = VarInt.readVLong(data, offset);
                longVal = ZigZag.decodeLong(longVal);
                return (int) (longVal ^ (longVal >>> 32));
            case REFERENCE:
                return VarInt.readVInt(data, offset);
            case BYTES:
                int byteLen = VarInt.readVInt(data, offset);
                offset += VarInt.sizeOfVInt(byteLen);
                return HashCodes.hashCode(data, offset, byteLen);
            case STRING:
                int strByteLen = VarInt.readVInt(data, offset);
                offset += VarInt.sizeOfVInt(strByteLen);
                return getNaturalStringHashCode(data, offset, strByteLen);
            case BOOLEAN:
                if(VarInt.readVNull(data, offset))
                    return 0;
                return data.get(offset) == 1 ? 1231 : 1237;
            case DOUBLE:
                long longBits = data.readLongBits(offset);
                return (int) (longBits ^ (longBits >>> 32));
            case FLOAT:
                return data.readIntBits(offset);
            default:
                throw new IllegalArgumentException("Schema " + schema.getName() + " has unknown field type for field " + schema.getFieldName(fieldIdx) + ": " + schema.getFieldType(fieldIdx));
        }
    }

    private int getNaturalStringHashCode(SegmentedByteArray data, long offset, int len) {
        int hashCode = 0;
        long endOffset = len + offset;

        while(offset < endOffset) {
            int ch = VarInt.readVInt(data, offset);
            hashCode = hashCode * 31 + ch;
            offset += VarInt.sizeOfVInt(ch);
        }

        return hashCode;
    }

}
