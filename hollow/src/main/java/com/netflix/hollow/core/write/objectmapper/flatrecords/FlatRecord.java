package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import com.netflix.hollow.core.write.HollowWriteFieldUtils;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;

/**
 * Warning: Experimental.  the FlatRecord feature is subject to breaking changes.
 */
public class FlatRecord {

    final HollowSchemaIdentifierMapper schemaIdMapper;
    final ByteData data;
    final int dataStartByte;
    int dataEndByte;
    RecordPrimaryKey recordPrimaryKey;

    public FlatRecord(ByteData recordData, HollowSchemaIdentifierMapper schemaIdMapper) {
        this.data = recordData;
        this.recordPrimaryKey = null;
        this.schemaIdMapper = schemaIdMapper;

        int currentRecordPointer = 0;
        int locationOfTopRecord = VarInt.readVInt(recordData, currentRecordPointer);
        currentRecordPointer += VarInt.sizeOfVInt(locationOfTopRecord);

        dataEndByte = VarInt.readVInt(recordData, currentRecordPointer);
        currentRecordPointer += VarInt.sizeOfVInt(dataEndByte);
        dataStartByte = currentRecordPointer;
        dataEndByte += dataStartByte + locationOfTopRecord;

        int topRecordSchemaId = VarInt.readVInt(recordData, dataStartByte + locationOfTopRecord);
        HollowSchema topRecordSchema = schemaIdMapper.getSchema(topRecordSchemaId);

        if (topRecordSchema.getSchemaType() == SchemaType.OBJECT) {
            PrimaryKey primaryKey = ((HollowObjectSchema) topRecordSchema).getPrimaryKey();

            if (primaryKey != null) {
                Object[] recordPrimaryKey = new Object[primaryKey.numFields()];
                FieldType primaryKeyFieldTypes[] = schemaIdMapper.getPrimaryKeyFieldTypes(topRecordSchemaId);
                int primaryKeyRecordPointer = dataEndByte;

                for (int i = 0; i < recordPrimaryKey.length; i++) {
                    int locationOfField = VarInt.readVInt(recordData, primaryKeyRecordPointer);
                    primaryKeyRecordPointer += VarInt.sizeOfVInt(locationOfField);

                    recordPrimaryKey[i] = readPrimaryKeyField(locationOfField + dataStartByte, primaryKeyFieldTypes[i]);
                }
                
                this.recordPrimaryKey = new RecordPrimaryKey(topRecordSchema.getName(), recordPrimaryKey);
            }
        }
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
            long longBits = HollowWriteFieldUtils.readLongBits(data, location);
            return Double.longBitsToDouble(longBits);
        case FLOAT:
            int intBits = HollowWriteFieldUtils.readIntBits(data, location);
            return Float.intBitsToFloat(intBits);
        case STRING:
            int length = VarInt.readVInt(data, location);
            location += VarInt.sizeOfVInt(length);
            char s[] = new char[length]; 
            
            for(int i=0;i<length;i++) {
                s[i] = (char)VarInt.readVInt(data, location);
                location += VarInt.sizeOfVInt(length);
            }
            
            return new String(s);
        case BYTES:
            length = VarInt.readVInt(data, location);
            location += VarInt.sizeOfVInt(length);
            byte b[] = new byte[length];
            
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
