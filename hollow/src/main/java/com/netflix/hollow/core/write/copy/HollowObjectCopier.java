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
package com.netflix.hollow.core.write.copy;

import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.tools.combine.OrdinalRemapper;

public class HollowObjectCopier extends HollowRecordCopier {

    private final int fieldIndexMapping[];

    public HollowObjectCopier(HollowObjectTypeReadState readTypeState, HollowObjectSchema destinationSchema, OrdinalRemapper ordinalRemapper) {
        super(readTypeState, new HollowObjectWriteRecord(destinationSchema), ordinalRemapper, false);

        fieldIndexMapping = new int[destinationSchema.numFields()];

        for(int i = 0; i < fieldIndexMapping.length; i++) {
            String fieldName = destinationSchema.getFieldName(i);
            fieldIndexMapping[i] = readState().getSchema().getPosition(fieldName);
        }
    }

    @Override
    public HollowWriteRecord copy(int ordinal) {
        HollowObjectTypeReadState objectReadState = readState();
        HollowObjectWriteRecord rec = rec();
        rec.reset();

        for(int i = 0; i < rec.getSchema().numFields(); i++) {
            String fieldName = rec.getSchema().getFieldName(i);
            int readFieldIndex = fieldIndexMapping[i];
            if(readFieldIndex != -1) {
                switch(rec().getSchema().getFieldType(i)) {
                    case BOOLEAN:
                        Boolean bool = objectReadState.readBoolean(ordinal, readFieldIndex);
                        if(bool != null)
                            rec.setBoolean(fieldName, bool.booleanValue());
                        break;
                    case BYTES:
                        byte[] bytes = objectReadState.readBytes(ordinal, readFieldIndex);
                        if(bytes != null)
                            rec.setBytes(fieldName, bytes);
                        break;
                    case STRING:
                        String str = objectReadState.readString(ordinal, readFieldIndex);
                        if(str != null)
                            rec.setString(fieldName, str);
                        break;
                    case DOUBLE:
                        double doubleVal = objectReadState.readDouble(ordinal, readFieldIndex);
                        if(!Double.isNaN(doubleVal))
                            rec.setDouble(fieldName, doubleVal);
                        break;
                    case FLOAT:
                        float floatVal = (float) objectReadState.readFloat(ordinal, readFieldIndex);
                        if(!Float.isNaN(floatVal))
                            rec.setFloat(fieldName, floatVal);
                        break;
                    case INT:
                        int intVal = objectReadState.readInt(ordinal, readFieldIndex);
                        if(intVal != Integer.MIN_VALUE)
                            rec.setInt(fieldName, intVal);
                        break;
                    case LONG:
                        long longVal = objectReadState.readLong(ordinal, readFieldIndex);
                        if(longVal != Long.MIN_VALUE)
                            rec.setLong(fieldName, longVal);
                        break;
                    case REFERENCE:
                        int ordinalVal = objectReadState.readOrdinal(ordinal, readFieldIndex);
                        if(ordinalVal >= 0) {
                            int remappedOrdinalVal = ordinalRemapper.getMappedOrdinal(readState().getSchema().getReferencedType(readFieldIndex), ordinalVal);
                            rec.setReference(fieldName, remappedOrdinalVal);
                        }
                        break;
                }
            }
        }
        return rec;
    }

    private HollowObjectTypeReadState readState() {
        return (HollowObjectTypeReadState) readTypeState;
    }

    private HollowObjectWriteRecord rec() {
        return (HollowObjectWriteRecord) writeRecord;
    }

}
