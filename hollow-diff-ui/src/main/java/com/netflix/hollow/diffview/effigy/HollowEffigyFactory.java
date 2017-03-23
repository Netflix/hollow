/*
 *
 *  Copyright 2016 Netflix, Inc.
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;

public class HollowEffigyFactory {

    private final Base64 base64 = new Base64();
    private final Map<HollowEffigy.Field, HollowEffigy.Field> fieldMemoizer = new HashMap<HollowEffigy.Field, HollowEffigy.Field>();

    public HollowEffigy effigy(HollowDataAccess dataAccess, String typeName, int ordinal) {
        if(ordinal == -1)
            return null;

        HollowTypeDataAccess typeState = dataAccess.getTypeDataAccess(typeName, ordinal);

        if(typeState == null)
            return null;

        if(typeState instanceof HollowObjectTypeDataAccess) {
            return objectEffigy((HollowObjectTypeDataAccess) typeState, ordinal);
        } else if(typeState instanceof HollowCollectionTypeDataAccess) {
            return collectionEffigy((HollowCollectionTypeDataAccess) typeState, ordinal);
        } else if(typeState instanceof HollowMapTypeDataAccess){
            return mapEffigy((HollowMapTypeDataAccess) typeState, ordinal);
        }

        throw new IllegalArgumentException("I don't know how to effigize a " + typeState.getClass());
    }


    private HollowEffigy objectEffigy(HollowObjectTypeDataAccess typeDataAccess, int ordinal) {
        HollowObjectSchema schema = typeDataAccess.getSchema();
        HollowEffigy effigy = new HollowEffigy(typeDataAccess, ordinal);

        for(int i=0;i<schema.numFields();i++) {
            String fieldName = schema.getFieldName(i);
            String fieldType = schema.getFieldType(i) == FieldType.REFERENCE ? schema.getReferencedType(i) : schema.getFieldType(i).toString();
            Object fieldValue = null;

            switch(schema.getFieldType(i)) {
            case BOOLEAN:
                fieldValue = typeDataAccess.readBoolean(ordinal, i);
                break;
            case BYTES:
                fieldValue = base64.encodeToString(typeDataAccess.readBytes(ordinal, i));
                break;
            case DOUBLE:
                fieldValue = Double.valueOf(typeDataAccess.readDouble(ordinal, i));
                break;
            case FLOAT:
                fieldValue = Float.valueOf(typeDataAccess.readFloat(ordinal, i));
                break;
            case INT:
                fieldValue = Integer.valueOf(typeDataAccess.readInt(ordinal, i));
                break;
            case LONG:
                long longVal = typeDataAccess.readLong(ordinal, i);
                if(longVal != Long.MIN_VALUE && "Date".equals(typeDataAccess.getSchema().getName()))
                    fieldValue = new Date(longVal).toString();
                else
                    fieldValue = Long.valueOf(typeDataAccess.readLong(ordinal, i));
                break;
            case STRING:
                fieldValue = typeDataAccess.readString(ordinal, i);
                break;
            case REFERENCE:
                fieldValue = effigy(typeDataAccess.getDataAccess(), schema.getReferencedType(i), typeDataAccess.readOrdinal(ordinal, i));
            }

            Field field = new Field(fieldName, fieldType, fieldValue);
            if(schema.getFieldType(i) != FieldType.REFERENCE)
                field = memoize(field);
            
            effigy.add(field);
        }

        return effigy;
    }

    private HollowEffigy collectionEffigy(HollowCollectionTypeDataAccess typeDataAccess, int ordinal) {
        HollowCollectionSchema schema = typeDataAccess.getSchema();
        HollowEffigy effigy = new HollowEffigy(typeDataAccess, ordinal);

        HollowOrdinalIterator iter = typeDataAccess.ordinalIterator(ordinal);
        int elementOrdinal = iter.next();
        while(elementOrdinal != NO_MORE_ORDINALS) {
            HollowEffigy elementEffigy = effigy(typeDataAccess.getDataAccess(), schema.getElementType(), elementOrdinal);
            effigy.add(new Field("element", elementEffigy));

            elementOrdinal = iter.next();
        }

        return effigy;
    }

    private HollowEffigy mapEffigy(HollowMapTypeDataAccess typeDataAccess, int ordinal) {
        HollowMapSchema schema = typeDataAccess.getSchema();
        HollowEffigy effigy = new HollowEffigy(typeDataAccess, ordinal);

        HollowMapEntryOrdinalIterator iter = typeDataAccess.ordinalIterator(ordinal);
        while(iter.next()) {
            HollowEffigy entryEffigy = new HollowEffigy("Map.Entry");
            entryEffigy.add(new Field("key", effigy(typeDataAccess.getDataAccess(), schema.getKeyType(), iter.getKey())));
            entryEffigy.add(new Field("value", effigy(typeDataAccess.getDataAccess(), schema.getValueType(), iter.getValue())));
            effigy.add(new Field("entry", "Map.Entry", entryEffigy));
        }

        return effigy;
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
