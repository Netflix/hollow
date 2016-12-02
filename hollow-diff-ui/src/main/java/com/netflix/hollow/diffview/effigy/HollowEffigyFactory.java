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
import static com.netflix.hollow.diffview.effigy.HollowEffigy.CollectionType.COLLECTION;
import static com.netflix.hollow.diffview.effigy.HollowEffigy.CollectionType.MAP;

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
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;

public class HollowEffigyFactory {

    private final Base64 base64 = new Base64();
    private final Map<HollowEffigy.Field, HollowEffigy.Field> fieldMemoizer = new HashMap<HollowEffigy.Field, HollowEffigy.Field>();
    private final Map<HollowDiffNodeIdentifier, HollowDiffNodeIdentifier> diffNodeIdMemoizer = new HashMap<HollowDiffNodeIdentifier, HollowDiffNodeIdentifier>();

    public HollowEffigy effigy(HollowDataAccess dataAccess, String typeName, int ordinal) {
        return effigy(dataAccess, typeName, ordinal, memoize(new HollowDiffNodeIdentifier(typeName)));
    }

    private HollowEffigy effigy(HollowDataAccess dataAccess, String typeName, int ordinal, HollowDiffNodeIdentifier identifier) {
        if(ordinal == -1)
            return null;

        HollowTypeDataAccess typeState = dataAccess.getTypeDataAccess(typeName, ordinal);

        if(typeState == null)
            return null;

        if(typeState instanceof HollowObjectTypeDataAccess) {
            return objectEffigy((HollowObjectTypeDataAccess) typeState, ordinal, identifier);
        } else if(typeState instanceof HollowCollectionTypeDataAccess) {
            return collectionEffigy((HollowCollectionTypeDataAccess) typeState, ordinal, identifier);
        } else if(typeState instanceof HollowMapTypeDataAccess){
            return mapEffigy((HollowMapTypeDataAccess) typeState, ordinal, identifier);
        }

        throw new IllegalArgumentException("I don't know how to effigize a " + typeState.getClass());
    }


    private HollowEffigy objectEffigy(HollowObjectTypeDataAccess typeDataAccess, int ordinal, HollowDiffNodeIdentifier identifier) {
        HollowObjectSchema schema = typeDataAccess.getSchema();
        HollowEffigy effigy = new HollowEffigy(schema.getName());

        for(int i=0;i<schema.numFields();i++) {
            String fieldName = schema.getFieldName(i);
            String fieldType = schema.getFieldType(i) == FieldType.REFERENCE ? schema.getReferencedType(i) : schema.getFieldType(i).toString();
            HollowDiffNodeIdentifier fieldIdentifier = memoize(new HollowDiffNodeIdentifier(identifier, fieldName, fieldType));
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
                fieldValue = effigy(typeDataAccess.getDataAccess(), schema.getReferencedType(i), typeDataAccess.readOrdinal(ordinal, i), fieldIdentifier);
            }

            if(schema.getFieldType(i) == FieldType.REFERENCE)
                effigy.add(new Field(fieldIdentifier, fieldValue));
            else
                effigy.add(memoize(new Field(fieldIdentifier, fieldValue)));
        }

        return effigy;
    }

    private HollowEffigy collectionEffigy(HollowCollectionTypeDataAccess typeDataAccess, int ordinal, HollowDiffNodeIdentifier identifier) {
        HollowCollectionSchema schema = typeDataAccess.getSchema();
        HollowDiffNodeIdentifier elementFieldIdentifier = memoize(new HollowDiffNodeIdentifier(identifier, "element", schema.getElementType()));
        HollowEffigy effigy = new HollowEffigy(schema.getName(), COLLECTION);

        HollowOrdinalIterator iter = typeDataAccess.ordinalIterator(ordinal);
        int elementOrdinal = iter.next();
        while(elementOrdinal != NO_MORE_ORDINALS) {
            HollowEffigy elementEffigy = effigy(typeDataAccess.getDataAccess(), schema.getElementType(), elementOrdinal, elementFieldIdentifier);
            effigy.add(new Field(elementFieldIdentifier, elementEffigy));

            elementOrdinal = iter.next();
        }

        return effigy;
    }

    private HollowEffigy mapEffigy(HollowMapTypeDataAccess typeDataAccess, int ordinal, HollowDiffNodeIdentifier identifier) {
        HollowMapSchema schema = typeDataAccess.getSchema();
        HollowDiffNodeIdentifier entryFieldIdentifier = memoize(new HollowDiffNodeIdentifier(identifier, "entry", "Map.Entry"));
        HollowDiffNodeIdentifier keyFieldIdentifier = memoize(new HollowDiffNodeIdentifier(identifier, "key", schema.getKeyType()));
        HollowDiffNodeIdentifier valueFieldIdentifier = memoize(new HollowDiffNodeIdentifier(identifier, "value", schema.getValueType()));
        HollowEffigy effigy = new HollowEffigy(schema.getName(), MAP);

        HollowMapEntryOrdinalIterator iter = typeDataAccess.ordinalIterator(ordinal);
        while(iter.next()) {
            HollowEffigy entryEffigy = new HollowEffigy("Map.Entry");
            entryEffigy.add(new Field(keyFieldIdentifier, effigy(typeDataAccess.getDataAccess(), schema.getKeyType(), iter.getKey(), keyFieldIdentifier)));
            entryEffigy.add(new Field(valueFieldIdentifier, effigy(typeDataAccess.getDataAccess(), schema.getValueType(), iter.getValue(), valueFieldIdentifier)));
            effigy.add(new Field(entryFieldIdentifier, entryEffigy));
        }

        return effigy;
    }

    private HollowDiffNodeIdentifier memoize(HollowDiffNodeIdentifier fieldId) {
        HollowDiffNodeIdentifier canonical = diffNodeIdMemoizer.get(fieldId);
        if(canonical == null) {
            diffNodeIdMemoizer.put(fieldId, fieldId);
            canonical = fieldId;
        }
        return canonical;
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
