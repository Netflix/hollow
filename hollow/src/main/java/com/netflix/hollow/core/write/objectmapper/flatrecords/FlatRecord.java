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

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;
import java.util.Arrays;

/**
 * Warning: Experimental.  the FlatRecord feature is subject to breaking changes.
 */
public class FlatRecord {

    final HollowSchemaIdentifierMapper schemaIdMapper;
    final ByteData data;
    final int dataStartByte;
    final int dataEndByte;
    final RecordPrimaryKey recordPrimaryKey;

    public FlatRecord(ByteData recordData, HollowSchemaIdentifierMapper schemaIdMapper) {
        this.data = recordData;
        this.schemaIdMapper = schemaIdMapper;

        int currentRecordPointer = 0;
        int locationOfTopRecord = VarInt.readVInt(recordData, currentRecordPointer);
        currentRecordPointer += VarInt.sizeOfVInt(locationOfTopRecord);

        int end = VarInt.readVInt(recordData, currentRecordPointer);
        currentRecordPointer += VarInt.sizeOfVInt(end);
        this.dataStartByte = currentRecordPointer;
        this.dataEndByte  = end + dataStartByte + locationOfTopRecord;

        int topRecordSchemaId = VarInt.readVInt(recordData, dataStartByte + locationOfTopRecord);
        HollowSchema topRecordSchema = schemaIdMapper.getSchema(topRecordSchemaId);

        if (topRecordSchema.getSchemaType() == SchemaType.OBJECT) {
            PrimaryKey primaryKey = ((HollowObjectSchema) topRecordSchema).getPrimaryKey();

            if (primaryKey != null) {
                Object[] recordPrimaryKey = new Object[primaryKey.numFields()];
                FieldType[] primaryKeyFieldTypes = schemaIdMapper.getPrimaryKeyFieldTypes(topRecordSchemaId);
                int primaryKeyRecordPointer = dataEndByte;

                for (int i = 0; i < recordPrimaryKey.length; i++) {
                    int locationOfField = VarInt.readVInt(recordData, primaryKeyRecordPointer);
                    primaryKeyRecordPointer += VarInt.sizeOfVInt(locationOfField);

                    recordPrimaryKey[i] = readPrimaryKeyField(locationOfField + dataStartByte, primaryKeyFieldTypes[i]);
                }
                
                this.recordPrimaryKey = new RecordPrimaryKey(topRecordSchema.getName(), recordPrimaryKey);
            } else {
                this.recordPrimaryKey = null;
            }
        } else {
            this.recordPrimaryKey = null;
        }
    }
    
    public byte[] toArray() {
        byte[] arr = new byte[(int)data.length()];
        
        for(int i=0;i<arr.length;i++) {
            arr[i] = data.get(i);
        }
        
        return arr;
    }

    private Object readPrimaryKeyField(int location, FieldType fieldType) {
        /// assumption: primary key fields are never null
        switch (fieldType) {
        case BOOLEAN:
            return data.get(location) == 1;
        case INT:
            return ZigZag.decodeInt(VarInt.readVInt(data, location));
        case LONG:
            return ZigZag.decodeLong(VarInt.readVLong(data, location));
        case DOUBLE:
            long longBits = data.readLongBits(location);
            return Double.longBitsToDouble(longBits);
        case FLOAT:
            int intBits = data.readIntBits(location);
            return Float.intBitsToFloat(intBits);
        case STRING:
            int length = VarInt.readVInt(data, location);
            location += VarInt.sizeOfVInt(length);
            
            int endLocation = location + length;
            
            char[] s = new char[length];
            int cnt = 0;
            
            while(location < endLocation) {
                int c = VarInt.readVInt(data, location);
                s[cnt] = (char)c;
                location += VarInt.sizeOfVInt(c);
                cnt++;
            }
            
            if(cnt < s.length)
                s = Arrays.copyOf(s, cnt);
            
            return new String(s);
        case BYTES:
            length = VarInt.readVInt(data, location);
            location += VarInt.sizeOfVInt(length);
            byte[] b = new byte[length];
            
            for(int i=0;i<b.length;i++) {
                b[i] = data.get(location++);
            }
            
            return b;
        case REFERENCE:
        default:
            throw new IllegalStateException("Should not have encoded primary key with REFERENCE type fields.");
        }

    }
    
    public RecordPrimaryKey getRecordPrimaryKey() {
        return recordPrimaryKey;
    }
    
}
