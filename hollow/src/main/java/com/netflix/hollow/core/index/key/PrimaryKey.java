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
package com.netflix.hollow.core.index.key;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.hollow.core.HollowDataset;
import java.util.Arrays;

/**
 * A PrimaryKey defines a set of one or more field(s) which should be unique for each record of a specific type.
 * <p>
 * The field definitions in a primary key may be hierarchical (traverse multiple record types) via dot-notation.  For example,
 * the field definition <i>movie.country.id</i> may be used to traverse a child record referenced by the field <i>movie</i>, its child
 * record referenced by the field <i>country</i>, and finally the country's field <i>id</i>. 
 * <p>
 *
 */
public class PrimaryKey {

    private final String type;
    private final String[] fieldPaths;

    /**
     * Define a PrimaryKey, which specifies a set of one or more field(s) which should be unique for each record of the given type. 
     * 
     * @param type
     * @param fieldPaths  The field definitions in a primary key may be hierarchical (traverse multiple record types) via dot-notation.  
     * For example, the field definition <i>movie.country.id</i> may be used to traverse a child record referenced by the field <i>movie</i>, 
     * its child record referenced by the field <i>country</i>, and finally the country's field <i>id</i>. 
     */
    public PrimaryKey(String type, String... fieldPaths) {
        if (fieldPaths == null || fieldPaths.length == 0) throw new IllegalArgumentException("fieldPaths can't not be null or empty");

        this.type = type;
        this.fieldPaths = fieldPaths;
    }

    public String getType() {
        return type;
    }

    public int numFields() {
        return fieldPaths.length;
    }

    public String getFieldPath(int idx) {
        return fieldPaths[idx];
    }

    public String[] getFieldPaths() {
        return fieldPaths;
    }

    public FieldType getFieldType(HollowDataset dataset, int fieldPathIdx) {
        return getFieldType(dataset, type, fieldPaths[fieldPathIdx]);
    }

    /**
     * The field path index is the object schemas' field positions for a particular field path. 
     */
    public int[] getFieldPathIndex(HollowDataset dataset, int fieldPathIdx) {
        return getFieldPathIndex(dataset, type, fieldPaths[fieldPathIdx]);
    }

    /**
     * Returns the ultimate field type of the specified type/field path in the provided dataset. 
     */
    public static FieldType getFieldType(HollowDataset dataAccess, String type, String fieldPath) {
        HollowObjectSchema schema = (HollowObjectSchema)dataAccess.getSchema(type);
        int pathIndexes[] = getFieldPathIndex(dataAccess, type, fieldPath);
        for(int i=0;i<pathIndexes.length - 1;i++)
            schema = (HollowObjectSchema)dataAccess.getSchema(schema.getReferencedType(pathIndexes[i]));
        return schema.getFieldType(pathIndexes[pathIndexes.length - 1]);
    }

    /**
     * The field path index is the object schemas' field positions for a particular field path. 
     */
    public static int[] getFieldPathIndex(HollowDataset dataset, String type, String fieldPath) {
        String paths[] = fieldPath.split("\\.");
        int pathIndexes[] = new int[paths.length];

        String refType = type;

        for(int i=0;i<paths.length;i++) {
            HollowObjectSchema schema = (HollowObjectSchema)dataset.getSchema(refType);
            try {
                pathIndexes[i] = schema.getPosition(paths[i]);
                refType = schema.getReferencedType(pathIndexes[i]);
            } catch (Exception ex) {
                throw new RuntimeException("Failed create path index for fieldPath=" + fieldPath + ", fieldName=" + paths[i] + " schema=" + schema.getName());
            }
        }

        return pathIndexes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(fieldPaths);
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PrimaryKey other = (PrimaryKey) obj;
        if (!Arrays.equals(fieldPaths, other.fieldPaths))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PrimaryKey [type=" + type + ", fieldPaths=" + Arrays.toString(fieldPaths) + "]";
    }
}
