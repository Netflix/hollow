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
package com.netflix.hollow.diffview.effigy;

import static com.netflix.hollow.core.read.iterator.HollowOrdinalIterator.NO_MORE_ORDINALS;

import com.netflix.hollow.core.read.dataaccess.HollowCollectionTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.diffview.effigy.HollowEffigy.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HollowEffigyFactory {

    private final Base64.Encoder base64 = Base64.getEncoder();
    private final Map<HollowEffigy.Field, HollowEffigy.Field> fieldMemoizer = new HashMap<HollowEffigy.Field, HollowEffigy.Field>();

    public HollowEffigy effigy(HollowDataAccess dataAccess, String typeName, int ordinal) {
        if(ordinal == -1)
            return null;

        HollowTypeDataAccess typeState = dataAccess.getTypeDataAccess(typeName, ordinal);

        if(typeState == null)
            return null;

        if(typeState instanceof HollowObjectTypeDataAccess) {
            return new HollowEffigy(this, (HollowObjectTypeDataAccess) typeState, ordinal);
        } else if(typeState instanceof HollowCollectionTypeDataAccess) {
            return new HollowEffigy(this, (HollowCollectionTypeDataAccess) typeState, ordinal);
        } else if(typeState instanceof HollowMapTypeDataAccess){
            return new HollowEffigy(this, (HollowMapTypeDataAccess) typeState, ordinal);
        }

        throw new IllegalArgumentException("I don't know how to effigize a " + typeState.getClass());
    }

    List<Field> createFields(HollowEffigy effigy) {
        switch(effigy.dataAccess.getSchema().getSchemaType()) {
        case OBJECT:
            return createObjectFields(effigy);
        case LIST:
        case SET:
            return createCollectionFields(effigy);
        case MAP:
            return createMapFields(effigy);
        }
        
        throw new IllegalArgumentException();
    }
    
    List<Field> createObjectFields(HollowEffigy effigy) {
        List<Field>fields = new ArrayList<Field>(); 
        
        HollowObjectTypeDataAccess typeDataAccess = (HollowObjectTypeDataAccess)effigy.dataAccess;
        
        HollowObjectSchema schema = typeDataAccess.getSchema();
        for(int i=0;i<schema.numFields();i++) {
            String fieldName = schema.getFieldName(i);
            String fieldType = schema.getFieldType(i) == FieldType.REFERENCE ? schema.getReferencedType(i) : schema.getFieldType(i).toString();
            Object fieldValue = null;

            switch(schema.getFieldType(i)) {
            case BOOLEAN:
                fieldValue = typeDataAccess.readBoolean(effigy.ordinal, i);
                break;
            case BYTES:
                byte[] fieldValueBytes = typeDataAccess.readBytes(effigy.ordinal, i);
                if (fieldValueBytes == null || fieldValueBytes.length == 0) {
                    fieldValue = fieldValueBytes;
                } else {
                    fieldValue = base64.encodeToString(fieldValueBytes);
                }
                break;
            case DOUBLE:
                fieldValue = Double.valueOf(typeDataAccess.readDouble(effigy.ordinal, i));
                break;
            case FLOAT:
                fieldValue = Float.valueOf(typeDataAccess.readFloat(effigy.ordinal, i));
                break;
            case INT:
                fieldValue = Integer.valueOf(typeDataAccess.readInt(effigy.ordinal, i));
                break;
            case LONG:
                long longVal = typeDataAccess.readLong(effigy.ordinal, i);
                if(longVal != Long.MIN_VALUE && "Date".equals(typeDataAccess.getSchema().getName()))
                    fieldValue = new Date(longVal).toString();
                else
                    fieldValue = Long.valueOf(typeDataAccess.readLong(effigy.ordinal, i));
                break;
            case STRING:
                fieldValue = typeDataAccess.readString(effigy.ordinal, i);
                break;
            case REFERENCE:
                fieldValue = effigy(typeDataAccess.getDataAccess(), schema.getReferencedType(i), typeDataAccess.readOrdinal(effigy.ordinal, i));
            }

            Field field = new Field(fieldName, fieldType, fieldValue);
            if(schema.getFieldType(i) != FieldType.REFERENCE)
                field = memoize(field);
            
            fields.add(field);
        }
        
        return fields;
    }

    private List<Field> createCollectionFields(HollowEffigy effigy) {
        List<Field> fields = new ArrayList<Field>();
        HollowCollectionTypeDataAccess typeDataAccess = (HollowCollectionTypeDataAccess) effigy.dataAccess;
        HollowCollectionSchema schema = typeDataAccess.getSchema();

        HollowOrdinalIterator iter = typeDataAccess.ordinalIterator(effigy.ordinal);
        int elementOrdinal = iter.next();
        while(elementOrdinal != NO_MORE_ORDINALS) {
            HollowEffigy elementEffigy = effigy(typeDataAccess.getDataAccess(), schema.getElementType(), elementOrdinal);
            fields.add(new Field("element", elementEffigy));

            elementOrdinal = iter.next();
        }
        
        return fields;
    }

    private List<Field> createMapFields(HollowEffigy effigy) {
        List<Field> fields = new ArrayList<Field>();
        HollowMapTypeDataAccess typeDataAccess = (HollowMapTypeDataAccess)effigy.dataAccess;
        HollowMapSchema schema = typeDataAccess.getSchema();
        HollowMapEntryOrdinalIterator iter = typeDataAccess.ordinalIterator(effigy.ordinal);
        while(iter.next()) {
            HollowEffigy entryEffigy = new HollowEffigy("Map.Entry");
            entryEffigy.add(new Field("key", effigy(typeDataAccess.getDataAccess(), schema.getKeyType(), iter.getKey())));
            entryEffigy.add(new Field("value", effigy(typeDataAccess.getDataAccess(), schema.getValueType(), iter.getValue())));
            fields.add(new Field("entry", "Map.Entry", entryEffigy));
        }
        
        return fields;
    }

    private HollowEffigy.Field memoize(HollowEffigy.Field field) {
        Field canonical = fieldMemoizer.get(field);
        if(canonical == null) {
            fieldMemoizer.put(field, field);
            canonical = field;
        }
        return canonical;
    }

}
