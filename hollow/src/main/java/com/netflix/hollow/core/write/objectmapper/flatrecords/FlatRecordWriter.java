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
package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.ArrayByteData;
import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.write.HollowHashableWriteRecord;
import com.netflix.hollow.core.write.HollowHashableWriteRecord.HashBehavior;
import com.netflix.hollow.core.write.HollowWriteRecord;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlatRecordWriter {
    private final HollowDataset dataset;
    private final HollowSchemaIdentifierMapper schemaIdMapper;
    private final ByteDataArray buf;

    private final Map<Integer, List<RecordLocation>> recordLocationsByHashCode;
    private final IntList recordLocationsByOrdinal;

    public FlatRecordWriter(HollowDataset dataset, HollowSchemaIdentifierMapper schemaIdMapper) {
        this.dataset = dataset;
        this.schemaIdMapper = schemaIdMapper;
        this.buf = new ByteDataArray();
        this.recordLocationsByOrdinal = new IntList();
        this.recordLocationsByHashCode = new HashMap<>();
    }

    public void reset() {
        buf.reset();
        recordLocationsByHashCode.clear();
        recordLocationsByOrdinal.clear();
    }

    public int write(HollowSchema schema, HollowWriteRecord rec) {
        int schemaOrdinal = schemaIdMapper.getSchemaId(schema);
        int nextRecordOrdinal = recordLocationsByOrdinal.size();

        int recStart = (int) buf.length();
        VarInt.writeVInt(buf, schemaOrdinal);
        if (rec instanceof HollowHashableWriteRecord)
            ((HollowHashableWriteRecord) rec).writeDataTo(buf, HashBehavior.IGNORED_HASHES);
        else
            rec.writeDataTo(buf);
        int recLen = (int) (buf.length() - recStart);

        Integer recordHashCode = HashCodes.hashCode(buf.getUnderlyingArray(), recStart, recLen);

        List<RecordLocation> existingRecLocs = recordLocationsByHashCode.get(recordHashCode);

        if (existingRecLocs == null) {
            RecordLocation newRecordLocation = new RecordLocation(nextRecordOrdinal, recStart, recLen);
            existingRecLocs = Collections.<RecordLocation>singletonList(newRecordLocation);
            recordLocationsByHashCode.put(recordHashCode, existingRecLocs);
            recordLocationsByOrdinal.add(recStart);

            return newRecordLocation.ordinal;
        } else {
            for (RecordLocation existing : existingRecLocs) {
                if (recLen == existing.len && buf.getUnderlyingArray().rangeEquals(recStart, buf.getUnderlyingArray(), existing.start, recLen)) {
                    buf.setPosition(recStart);
                    return existing.ordinal;
                }
            }

            RecordLocation newRecordLocation = new RecordLocation(nextRecordOrdinal, recStart, recLen);

            if (existingRecLocs.size() == 1) {
                List<RecordLocation> newRecLocs = new ArrayList<>(2);
                newRecLocs.add(existingRecLocs.get(0));
                newRecLocs.add(newRecordLocation);
                recordLocationsByHashCode.put(recordHashCode, newRecLocs);
            } else {
                existingRecLocs.add(newRecordLocation);
            }

            recordLocationsByOrdinal.add(recStart);

            return newRecordLocation.ordinal;
        }
    }

    public FlatRecord generateFlatRecord() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            writeTo(baos);
            byte[] arr = baos.toByteArray();
            ArrayByteData recordData = new ArrayByteData(arr);
            return new FlatRecord(recordData, schemaIdMapper);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void writeTo(OutputStream os) throws IOException {
        if (recordLocationsByOrdinal.size() == 0)
            throw new IOException("No data to write!");

        int locationOfTopRecord = recordLocationsByOrdinal.get(recordLocationsByOrdinal.size() - 1);
        int schemaIdOfTopRecord = VarInt.readVInt(buf.getUnderlyingArray(), locationOfTopRecord);
        HollowSchema schemaOfTopRecord = schemaIdMapper.getSchema(schemaIdOfTopRecord);

        VarInt.writeVInt(os, locationOfTopRecord);

        int pkFieldValueLocations[] = null;
        if (schemaOfTopRecord.getSchemaType() == SchemaType.OBJECT) {
            PrimaryKey primaryKey = ((HollowObjectSchema) schemaOfTopRecord).getPrimaryKey();
            if (primaryKey != null) {
                pkFieldValueLocations = new int[primaryKey.numFields()];
                /// encode the locations of the primary key fields
                for (int i = 0; i < primaryKey.numFields(); i++) {
                    int[] fieldPathIndex = primaryKey.getFieldPathIndex(dataset, i);

                    int pkFieldOffset = locatePrimaryKeyField(locationOfTopRecord, fieldPathIndex, 0);
                    if (pkFieldOffset == -1) {
                        throw new IllegalStateException("Cannot write FlatRecord: primary key field '" + primaryKey.getFieldPath(i) + "' is null");
                    }
                    pkFieldValueLocations[i] = pkFieldOffset;
                }
            }
        }

        VarInt.writeVInt(os, (int) buf.length() - locationOfTopRecord);

        buf.getUnderlyingArray().writeTo(os, 0, buf.length());

        if (pkFieldValueLocations != null) {
            for (int i = 0; i < pkFieldValueLocations.length; i++) {
                VarInt.writeVInt(os, pkFieldValueLocations[i]);
            }
        }
    }

    private int locatePrimaryKeyField(int locationOfCurrentRecord, int[] fieldPathIndex, int idx) {
        int schemaIdOfRecord = VarInt.readVInt(buf.getUnderlyingArray(), locationOfCurrentRecord);
        HollowObjectSchema recordSchema = (HollowObjectSchema) schemaIdMapper.getSchema(schemaIdOfRecord);
        locationOfCurrentRecord += VarInt.sizeOfVInt(schemaIdOfRecord);

        int fieldOffset = navigateToField(recordSchema, fieldPathIndex[idx], locationOfCurrentRecord);
        if (VarInt.readVNull(buf.getUnderlyingArray(), fieldOffset)) {
            return -1;
        }

        if (idx == fieldPathIndex.length - 1) {
            return fieldOffset;
        }

        int ordinalOfNextRecord = VarInt.readVInt(buf.getUnderlyingArray(), fieldOffset);
        if (ordinalOfNextRecord == -1) {
            return -1;
        }

        int offsetOfNextRecord = recordLocationsByOrdinal.get(ordinalOfNextRecord);

        return locatePrimaryKeyField(offsetOfNextRecord, fieldPathIndex, idx + 1);
    }

    private int navigateToField(HollowObjectSchema schema, int fieldIdx, int offset) {
        for (int i = 0; i < fieldIdx; i++) {
            switch (schema.getFieldType(i)) {
            case INT:
            case LONG:
            case REFERENCE:
                offset += VarInt.nextVLongSize(buf.getUnderlyingArray(), offset);
                break;
            case BYTES:
            case STRING:
                int fieldLength = VarInt.readVInt(buf.getUnderlyingArray(), offset);
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

    private static class RecordLocation {
        private final int ordinal;
        private final long start;
        private final int len;

        public RecordLocation(int ordinal, long start, int len) {
            this.ordinal = ordinal;
            this.start = start;
            this.len = len;
        }
    }
}
