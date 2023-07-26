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

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.HashMap;
import java.util.Map;

public class FlatRecordDumper {
    
    private final Map<Integer, Integer> ordinalMapping;
    private final Map<String, HollowWriteRecord> writeRecords;
    
    private final HollowWriteStateEngine stateEngine;
    
    private FlatRecord record;

    public FlatRecordDumper(HollowWriteStateEngine dumpTo) {
        this.ordinalMapping = new HashMap<>();
        this.writeRecords = new HashMap<>();
        this.stateEngine = dumpTo;
    }
    
    public void dump(FlatRecord record) {
        this.record = record;
        this.ordinalMapping.clear();
        
        int currentRecordPointer = record.dataStartByte;
        int currentRecordOrdinal = 0;
        
        while(currentRecordPointer < record.dataEndByte) {
            int currentSchemaId = VarInt.readVInt(record.data, currentRecordPointer);
            currentRecordPointer += VarInt.sizeOfVInt(currentSchemaId);
            HollowSchema recordSchema = record.schemaIdMapper.getSchema(currentSchemaId);
            HollowSchema engineSchema = stateEngine.getSchema(recordSchema.getName());
            
            /// copy this record, then map the ordinal
            /// if corresponding state is not available in state engine, skip the record.
            currentRecordPointer = copyRecord(recordSchema, engineSchema, currentRecordPointer, currentRecordOrdinal++);
        }
    }
    
    private int copyRecord(HollowSchema recordSchema, HollowSchema engineSchema, int currentRecordPointer, int currentRecordOrdinal) {
        switch(recordSchema.getSchemaType()) {
        case OBJECT:
            return copyObjectRecord((HollowObjectSchema)recordSchema, (HollowObjectSchema)engineSchema, currentRecordPointer, currentRecordOrdinal);
        case LIST:
            return copyListRecord((HollowListSchema)engineSchema, currentRecordPointer, currentRecordOrdinal);
        case SET:
            return copySetRecord((HollowSetSchema)engineSchema, currentRecordPointer, currentRecordOrdinal);
        case MAP:
            return copyMapRecord((HollowMapSchema)engineSchema, currentRecordPointer, currentRecordOrdinal);
        default:
            throw new IllegalStateException("Unknown schema type: " + recordSchema.getSchemaType());
        }
    }
    
    private int copyListRecord(HollowListSchema engineSchema, int currentRecordPointer, int currentRecordOrdinal) {
        HollowListWriteRecord rec = engineSchema != null ? (HollowListWriteRecord)getWriteRecord(engineSchema) : null;
        
        int numElements = VarInt.readVInt(record.data, currentRecordPointer);
        currentRecordPointer += VarInt.sizeOfVInt(numElements);
        
        for(int i=0;i<numElements;i++) {
            int unmappedElementOrdinal = VarInt.readVInt(record.data, currentRecordPointer);
            currentRecordPointer += VarInt.sizeOfVInt(unmappedElementOrdinal);
            
            if(rec != null) {
                int mappedElementOrdinal = ordinalMapping.get(unmappedElementOrdinal);
                rec.addElement(mappedElementOrdinal);
            }
        }
        
        if(engineSchema != null) {
            int stateEngineOrdinal = stateEngine.add(engineSchema.getName(), rec);
            ordinalMapping.put(currentRecordOrdinal, stateEngineOrdinal);
        }
        
        return currentRecordPointer;
    }
    
    private int copySetRecord(HollowSetSchema engineSchema, int currentRecordPointer, int currentRecordOrdinal) {
        HollowSetWriteRecord rec = engineSchema != null ? (HollowSetWriteRecord)getWriteRecord(engineSchema) : null;
        
        int numElements = VarInt.readVInt(record.data, currentRecordPointer);
        currentRecordPointer += VarInt.sizeOfVInt(numElements);
        
        int unmappedOrdinal = 0;
        
        for(int i=0;i<numElements;i++) {
            int unmappedOrdinalDelta = VarInt.readVInt(record.data, currentRecordPointer);
            currentRecordPointer += VarInt.sizeOfVInt(unmappedOrdinalDelta);
            unmappedOrdinal += unmappedOrdinalDelta;
            
            if(rec != null) {
                int mappedOrdinal = ordinalMapping.get(unmappedOrdinal);
                rec.addElement(mappedOrdinal);
            }
        }
        
        if(engineSchema != null) {
            int stateEngineOrdinal = stateEngine.add(engineSchema.getName(), rec);
            ordinalMapping.put(currentRecordOrdinal, stateEngineOrdinal);
        }
        
        return currentRecordPointer;
    }
    
    private int copyMapRecord(HollowMapSchema engineSchema, int currentRecordPointer, int currentRecordOrdinal) {
        HollowMapWriteRecord rec = engineSchema != null ? (HollowMapWriteRecord)getWriteRecord(engineSchema) : null;
        
        int numElements = VarInt.readVInt(record.data, currentRecordPointer);
        currentRecordPointer += VarInt.sizeOfVInt(numElements);
        
        int unmappedKeyOrdinal = 0;
        
        for(int i=0;i<numElements;i++) {
            int unmappedKeyOrdinalDelta = VarInt.readVInt(record.data, currentRecordPointer);
            currentRecordPointer += VarInt.sizeOfVInt(unmappedKeyOrdinalDelta);
            int unmappedValueOrdinal = VarInt.readVInt(record.data, currentRecordPointer);
            currentRecordPointer += VarInt.sizeOfVInt(unmappedValueOrdinal);

            unmappedKeyOrdinal += unmappedKeyOrdinalDelta;
            
            if(rec != null) {
                int mappedKeyOrdinal = ordinalMapping.get(unmappedKeyOrdinal);
                int mappedValueOrdinal = ordinalMapping.get(unmappedValueOrdinal);
                rec.addEntry(mappedKeyOrdinal, mappedValueOrdinal);
            }
        }
        
        if(engineSchema != null) {
            int stateEngineOrdinal = stateEngine.add(engineSchema.getName(), rec);
            ordinalMapping.put(currentRecordOrdinal, stateEngineOrdinal);
        }
        
        return currentRecordPointer;
    }
    
    private int copyObjectRecord(HollowObjectSchema recordSchema, HollowObjectSchema engineSchema, int currentRecordPointer, int currentRecordOrdinal) {
        HollowObjectWriteRecord rec = engineSchema != null ? (HollowObjectWriteRecord)getWriteRecord(engineSchema) : null;
        
        for(int i=0;i<recordSchema.numFields();i++) {
            String fieldName = recordSchema.getFieldName(i);
            FieldType fieldType = recordSchema.getFieldType(i);
            boolean fieldExistsInEngine = engineSchema != null && engineSchema.getPosition(fieldName) != -1; 
            
            currentRecordPointer = copyObjectField(fieldExistsInEngine ? rec : null, fieldName, fieldType, currentRecordPointer);
        }
        
        if(engineSchema != null) {
            int stateEngineOrdinal = stateEngine.add(engineSchema.getName(), rec);
            ordinalMapping.put(currentRecordOrdinal, stateEngineOrdinal);
        }

        return currentRecordPointer;
    }
    
    private int copyObjectField(HollowObjectWriteRecord rec, String fieldName, FieldType fieldType, int currentRecordPointer) {
        switch(fieldType) {
        case BOOLEAN:
            if(!VarInt.readVNull(record.data, currentRecordPointer)) {
                boolean value = record.data.get(currentRecordPointer) == 1;
                if(rec != null)
                    rec.setBoolean(fieldName, value);
            }
            
            return currentRecordPointer + 1;
        case INT:
            if(VarInt.readVNull(record.data, currentRecordPointer))
                return currentRecordPointer + 1;

            int ivalue = VarInt.readVInt(record.data, currentRecordPointer);
            currentRecordPointer += VarInt.sizeOfVInt(ivalue);
            if(rec != null)
                rec.setInt(fieldName, ZigZag.decodeInt(ivalue));
            
            return currentRecordPointer;
        case LONG:
            if(VarInt.readVNull(record.data, currentRecordPointer))
                return currentRecordPointer + 1;

            long lvalue = VarInt.readVLong(record.data, currentRecordPointer);
            currentRecordPointer += VarInt.sizeOfVLong(lvalue);
            if(rec != null)
                rec.setLong(fieldName, ZigZag.decodeLong(lvalue));
            
            return currentRecordPointer;
        case FLOAT:
            int intBits = record.data.readIntBits(currentRecordPointer);
            if(intBits != HollowObjectWriteRecord.NULL_FLOAT_BITS) {
                float fvalue = Float.intBitsToFloat(intBits);
                if(rec != null)
                    rec.setFloat(fieldName, fvalue);
            }
            
            return currentRecordPointer + 4;
        case DOUBLE:
            long longBits = record.data.readLongBits(currentRecordPointer);
            if(longBits != HollowObjectWriteRecord.NULL_DOUBLE_BITS) {
                double dvalue = Double.longBitsToDouble(longBits);
                if(rec != null)
                    rec.setDouble(fieldName, dvalue);
            }
            
            return currentRecordPointer + 8;
        case STRING:
            if(VarInt.readVNull(record.data, currentRecordPointer))
                return currentRecordPointer + 1;
            
            int length = VarInt.readVInt(record.data, currentRecordPointer);
            currentRecordPointer += VarInt.sizeOfVInt(length);
            
            int cLength = VarInt.countVarIntsInRange(record.data, currentRecordPointer, length);
            char[] s = new char[cLength];
            
            for(int i=0;i<cLength;i++) {
                int charValue = VarInt.readVInt(record.data, currentRecordPointer); 
                s[i] = (char)charValue;
                currentRecordPointer += VarInt.sizeOfVInt(charValue);
            }
            
            if(rec != null)
                rec.setString(fieldName, new String(s));
            return currentRecordPointer;
        case BYTES:
            if(VarInt.readVNull(record.data, currentRecordPointer))
                return currentRecordPointer + 1;
            
            length = VarInt.readVInt(record.data, currentRecordPointer);
            currentRecordPointer += VarInt.sizeOfVInt(length);
            byte[] b = new byte[length];
            
            for(int i=0;i<length;i++) {
                b[i] = record.data.get(currentRecordPointer++);
            }

            if(rec != null)
                rec.setBytes(fieldName, b);
            return currentRecordPointer;
        case REFERENCE:
            if(VarInt.readVNull(record.data, currentRecordPointer))
                return currentRecordPointer + 1;
            
            int unmappedOrdinal = VarInt.readVInt(record.data, currentRecordPointer);
            
            if(rec != null) {
//                if (ordinalMapping.get(unmappedOrdinal) == null) {
//                    throw new IllegalStateException("");
//                }
                int mappedOrdinal = ordinalMapping.get(unmappedOrdinal);
                rec.setReference(fieldName, mappedOrdinal);
            }
            
            return currentRecordPointer + VarInt.sizeOfVInt(unmappedOrdinal);
        default:
            throw new IllegalArgumentException("Unknown field type: " + fieldType);
        }
        
    }
    
    private HollowWriteRecord getWriteRecord(HollowSchema schema) {
        HollowWriteRecord rec = writeRecords.get(schema.getName());
        
        if(rec == null) {
            switch(schema.getSchemaType()) {
            case OBJECT: 
                rec = new HollowObjectWriteRecord((HollowObjectSchema)schema);
                break;
            case LIST: 
                rec = new HollowListWriteRecord();
                break;
            case SET: 
                rec = new HollowSetWriteRecord();
                break;
            case MAP: 
                rec = new HollowMapWriteRecord();
                break;
            }
            
            writeRecords.put(schema.getName(), rec);
        }
        
        rec.reset();
        return rec;
    }    

}
