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
package com.netflix.hollow.core.index.key;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.FieldPaths;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;

import java.util.Arrays;

/**
 * A PrimaryKey defines a set of one or more field(s) which should be unique for each record of a specific type.
 * <p>
 * The field definitions in a primary key may be hierarchical (traverse multiple record types) via dot-notation.  For example,
 * the field definition <i>movie.country.id</i> may be used to traverse a child record referenced by the field <i>movie</i>, its child
 * record referenced by the field <i>country</i>, and finally the country's field <i>id</i>.
 */
public class PrimaryKey {

    /**
     * Creates a primary key instance from the type and field paths. If no fields are specified, then this uses the
     * the schema attached to the HollowDataAccess to generate a primary key instance based on the
     * {@link com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey} defined for the type.
     *
     * This method is typically used for building indexes.
     *
     * @param hollowDataAccess hollow data access or state engine
     * @param type             hollow type
     * @param fieldPaths       field paths for fields that make up the primary key. If no fields are passed in, then create using schema definition of primary key
     * @return populated primary key
     */
    public static PrimaryKey create(HollowDataAccess hollowDataAccess, String type, String... fieldPaths) {
        if (fieldPaths != null && fieldPaths.length != 0) {
            return new PrimaryKey(type, fieldPaths);
        }

        HollowSchema schema = hollowDataAccess.getSchema(type);
        if (schema instanceof HollowObjectSchema) {
            return ((HollowObjectSchema) schema).getPrimaryKey();
        }
        return null;
    }

    private final String type;
    private final String[] fieldPaths;

    /**
     * Define a PrimaryKey, which specifies a set of one or more field(s) which should be unique for each record of the given type.
     *
     * @param type the type name
     * @param fieldPaths  The field definitions in a primary key may be hierarchical (traverse multiple record types) via dot-notation.
     * For example, the field definition <i>movie.country.id</i> may be used to traverse a child record referenced by the field <i>movie</i>,
     * its child record referenced by the field <i>country</i>, and finally the country's field <i>id</i>.
     */
    public PrimaryKey(String type, String... fieldPaths) {
        if (fieldPaths == null || fieldPaths.length == 0) {
            throw new IllegalArgumentException("fieldPaths can't not be null or empty");
        }

        this.type = type;
        this.fieldPaths = fieldPaths.clone();
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

    public HollowObjectSchema getFieldSchema(HollowDataset dataset, int fieldPathIdx) {
        return getFieldSchema(dataset, type, fieldPaths[fieldPathIdx]);
    }

    /**
     * The field path index is the object schemas' field positions for a particular field path.
     *
     * @param dataset the data set
     * @param fieldPathIdx the index to a field path string
     * @return the field path index
     */
    public int[] getFieldPathIndex(HollowDataset dataset, int fieldPathIdx) {
        return getFieldPathIndex(dataset, type, fieldPaths[fieldPathIdx]);
    }

    /**
     * Returns the ultimate field type of the specified type/field path in the provided dataset.
     *
     * @param dataAccess the data set
     * @param type the type name
     * @param fieldPath the field path
     * @return the field type
     */
    public static FieldType getFieldType(HollowDataset dataAccess, String type, String fieldPath) {
        HollowObjectSchema schema = (HollowObjectSchema)dataAccess.getSchema(type);
        int pathIndexes[] = getFieldPathIndex(dataAccess, type, fieldPath);
        for(int i=0;i<pathIndexes.length - 1;i++)
            schema = (HollowObjectSchema)dataAccess.getSchema(schema.getReferencedType(pathIndexes[i]));
        return schema.getFieldType(pathIndexes[pathIndexes.length - 1]);
    }

    /**
     * Returns the ultimate field Schema of the specified type/field path in the provided dataset.
     *
     * @param dataAccess the data set
     * @param type the type name
     * @param fieldPath the field path
     * @return the field schema
     */
    public static HollowObjectSchema getFieldSchema(HollowDataset dataAccess, String type, String fieldPath) {
        HollowObjectSchema schema = (HollowObjectSchema)dataAccess.getSchema(type);
        int pathIndexes[] = getFieldPathIndex(dataAccess, type, fieldPath);
        for (int i = 0; i < pathIndexes.length; i++)
            schema = (HollowObjectSchema)dataAccess.getSchema(schema.getReferencedType(pathIndexes[i]));
        return schema;
    }

    /**
     * Returns a separated field path, which has been auto-expanded if necessary based on the provided primary key field
     * path.
     *
     * @param dataset the data access
     * @param type the type name
     * @param fieldPath the field path
     * @return the separated field path
     */
    public static String[] getCompleteFieldPathParts(HollowDataset dataset, String type, String fieldPath) {
        int fieldPathIdx[] = getFieldPathIndex(dataset, type, fieldPath);
        String fieldPathParts[] = new String[fieldPathIdx.length];

        HollowObjectSchema schema = (HollowObjectSchema) dataset.getSchema(type);
        for(int i=0;i<fieldPathParts.length;i++) {
            fieldPathParts[i] = schema.getFieldName(fieldPathIdx[i]);
            schema = (HollowObjectSchema) dataset.getSchema(schema.getReferencedType(fieldPathIdx[i]));
        }

        return fieldPathParts;
    }

    /**
     * The field path index is the object schemas' field positions for a particular field path.
     *
     * @param dataset the data set
     * @param type the type name
     * @param fieldPath the field path string
     * @return the field path index
     */
    public static int[] getFieldPathIndex(HollowDataset dataset, String type, String fieldPath) {
        return FieldPaths.createFieldPathForPrimaryKey(dataset, type, fieldPath).getSegments().stream()
                .mapToInt(FieldPaths.ObjectFieldSegment::getIndex)
                .toArray();
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
