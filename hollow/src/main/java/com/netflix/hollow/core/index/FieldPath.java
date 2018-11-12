/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.core.index;

import static com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import static com.netflix.hollow.core.schema.HollowSchema.SchemaType;

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
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to represent a field path. A field path is a "." separated list of fields that are used for traversal when looking up values for a type and ordinal of that type.
 * It is used to read field values for a given type and its ordinal. It is convenient, when the path is deeply nested.
 * <p>
 * Upon initializing a instance of this class with given parameters, it does the following checks
 * <ul>
 * <li>validates the path by traversing it and making sure all types are present in the HollowDataAccess</li>
 * <li>auto-expands the path for collections (appending ".element" if missing), and a reference type (e.g String/Integer appending ".value" if missing) with single field. (except for Map schema)</li>
 * <li>for map schema type, the class looks for "key" to iterate over key types and "value" to iterate over value types.</li>
 * </ul>
 * <p>
 * This class has a convenience method to find values following the field path and given a start field position.
 */
public class FieldPath {

    private final String fieldPath;
    private final HollowDataAccess hollowDataAccess;
    private final String type;

    private String[] fields;
    private int[] fieldPositions;
    private FieldType[] fieldTypes;
    private String lastRefTypeInPath;

    private boolean autoExpand;

    /**
     * Create new FieldPath with auto-expand feature.
     *
     * @param hollowDataAccess hollow data access
     * @param type             parent type of the field path
     * @param fieldPath        "." separated fields
     */
    public FieldPath(HollowDataAccess hollowDataAccess, String type, String fieldPath) {
        this(hollowDataAccess, type, fieldPath, true);
    }


    /**
     * Create new FieldPath.
     *
     * @param hollowDataAccess hollow data access
     * @param type             parent type of the field path
     * @param fieldPath        "." separated fields
     * @param autoExpand       if the field path should be auto-expand collections and references with one field.
     */
    public FieldPath(HollowDataAccess hollowDataAccess, String type, String fieldPath, boolean autoExpand) {
        this.fieldPath = fieldPath;
        this.hollowDataAccess = hollowDataAccess;
        this.type = type;
        this.autoExpand = autoExpand;
        initialize();
    }

    private void initialize() {
        String[] fieldParts = fieldPath.split("\\.");
        List<String> fields = new ArrayList<>();
        List<Integer> fieldPositions = new ArrayList<>();
        List<FieldType> fieldTypes = new ArrayList<>();

        // traverse through the field path to save field position and types.
        String refType = type;
        String lastRefType = type;
        int i = 0;
        while (i < fieldParts.length || refType != null) {

            HollowSchema schema = hollowDataAccess.getSchema(refType);
            if (schema == null)
                throw new IllegalArgumentException("Schema not found for the type : " + refType);
            SchemaType schemaType = hollowDataAccess.getSchema(refType).getSchemaType();

            String fieldName = null;
            int fieldPosition = 0;
            FieldType fieldType = FieldType.REFERENCE;

            if (schemaType.equals(SchemaType.OBJECT)) {

                HollowObjectSchema objectSchema = (HollowObjectSchema) schema;

                // find field position, field name and field type
                if (i >= fieldParts.length) {
                    if (!autoExpand || objectSchema.numFields() != 1)
                        throw new IllegalArgumentException("Incomplete field path at type :" + refType + ". Please enter the field names in type to complete the path.");
                    fieldPosition = 0;
                } else {
                    fieldPosition = objectSchema.getPosition(fieldParts[i]);
                    if (fieldPosition < 0)
                        throw new IllegalArgumentException("Could not find a valid field position for the field :" + fieldParts[i] + " in type :" + refType);
                    i++; // increment index for field part for next iteration.
                }

                fieldName = objectSchema.getFieldName(fieldPosition);
                fieldType = objectSchema.getFieldType(fieldPosition);

                // check field type.
                if (fieldType.equals(FieldType.REFERENCE)) {
                    refType = objectSchema.getReferencedType(fieldName);
                } else if (i >= fieldParts.length) {
                    // reached the end of field path
                    lastRefType = refType;
                    refType = null;
                } else {
                    // if not end of the field path and found a value type, then throw the exception.
                    throw new IllegalArgumentException("Field path should contain references and should lead to a field primitive field type");
                }

            } else if (schemaType.equals(SchemaType.LIST) || schemaType.equals(SchemaType.SET)) {

                if (autoExpand && (i >= fieldParts.length || (i < fieldParts.length && !fieldParts[i].equals("element")))) {
                    fieldName = "element";
                } else {
                    fieldName = fieldParts[i];
                    i++; // increment index for field part for next iteration.
                }
                // update ref type to element type.
                refType = ((HollowCollectionSchema) schema).getElementType();

            } else if (schemaType.equals(SchemaType.MAP)) {

                if ((i >= fieldParts.length) || (!fieldParts[i].equals("value") && !fieldParts[i].equals("key")))
                    throw new IllegalArgumentException("When using Map in field path, please specify key or value. Cannot auto-expand on Map type field");

                // update field name and increment i to move to next field
                fieldName = fieldParts[i];
                i++;

                // update ref type depending on key or value in field path for map.
                if (fieldName.equals("value")) {
                    refType = ((HollowMapSchema) schema).getValueType();
                } else refType = ((HollowMapSchema) schema).getKeyType();
            }

            // update lists
            fields.add(fieldName);
            fieldPositions.add(fieldPosition);
            fieldTypes.add(fieldType);
        }

        this.fields = fields.toArray(new String[fields.size()]);
        this.fieldPositions = new int[fieldPositions.size()];
        for (i = 0; i < fieldPositions.size(); i++) this.fieldPositions[i] = fieldPositions.get(i);
        this.fieldTypes = fieldTypes.toArray(new FieldType[fieldTypes.size()]);
        this.lastRefTypeInPath = lastRefType;
    }

    public String getLastRefTypeInPath() {
        return lastRefTypeInPath;
    }

    public FieldType getLastFieldType() {
        return fieldTypes[this.fields.length - 1];
    }

    /**
     * Recursively find all the values following the field path.
     *
     * @param ordinal ordinal record for the given type in field path
     * @return Array of values found at the field path for the given ordinal record in the type.
     */
    public Object[] findValues(int ordinal) {
        return getAllValues(ordinal, type, 0);
    }

    /**
     * Recursively find a value following the path. If the path contains a collection, then the first value is picked.
     *
     * @param ordinal the ordinal used to find a value
     * @return A value found at the field path for the given ordinal record in the type.
     */
    public Object findValue(int ordinal) {
        return getValue(ordinal, type, 0);
    }

    private Object getValue(int ordinal, String type, int fieldIndex) {
        Object value = null;
        HollowTypeDataAccess typeDataAccess = hollowDataAccess.getTypeDataAccess(type);
        SchemaType schemaType = hollowDataAccess.getSchema(type).getSchemaType();
        HollowSchema schema = hollowDataAccess.getSchema(type);

        if (schemaType.equals(SchemaType.LIST) || schemaType.equals(SchemaType.SET)) {

            HollowCollectionTypeDataAccess collectionTypeDataAccess = (HollowCollectionTypeDataAccess) typeDataAccess;
            HollowCollectionSchema collectionSchema = (HollowCollectionSchema) schema;
            String elementType = collectionSchema.getElementType();

            HollowOrdinalIterator it = collectionTypeDataAccess.ordinalIterator(ordinal);
            int refOrdinal = it.next();
            if (refOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                value = getValue(refOrdinal, elementType, fieldIndex + 1);
            }
            return value;

        } else if (schemaType.equals(SchemaType.MAP)) {
            // Map type
            HollowMapTypeDataAccess mapTypeDataAccess = (HollowMapTypeDataAccess) typeDataAccess;
            HollowMapSchema mapSchema = (HollowMapSchema) schema;

            // what to iterate on in map
            boolean iterateThroughKeys = fields[fieldIndex].equals("key");
            String keyOrValueType = iterateThroughKeys ? mapSchema.getKeyType() : mapSchema.getValueType();

            HollowMapEntryOrdinalIterator mapEntryIterator = mapTypeDataAccess.ordinalIterator(ordinal);
            if (mapEntryIterator.next()) {
                int keyOrValueOrdinal = iterateThroughKeys ? mapEntryIterator.getKey() : mapEntryIterator.getValue();
                value = getValue(keyOrValueOrdinal, keyOrValueType, fieldIndex + 1);
            }
            return value;

        } else {
            // Object type
            HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
            HollowObjectTypeDataAccess objectTypeDataAccess = (HollowObjectTypeDataAccess) typeDataAccess;

            if (fieldTypes[fieldIndex].equals(FieldType.REFERENCE)) {

                int refOrdinal = objectTypeDataAccess.readOrdinal(ordinal, fieldPositions[fieldIndex]);
                if (refOrdinal >= 0) {
                    String refType = objectSchema.getReferencedType(fieldPositions[fieldIndex]);
                    value = getValue(refOrdinal, refType, fieldIndex + 1);
                }

            } else {
                value = readFromObject(objectTypeDataAccess, ordinal, fieldTypes[fieldIndex], fieldPositions[fieldIndex]);
            }
        }
        return value;
    }


    private Object[] getAllValues(int ordinal, String type, int fieldIndex) {
        Object[] values;
        HollowTypeDataAccess typeDataAccess = hollowDataAccess.getTypeDataAccess(type);
        SchemaType schemaType = hollowDataAccess.getSchema(type).getSchemaType();
        HollowSchema schema = hollowDataAccess.getSchema(type);

        if (schemaType.equals(SchemaType.LIST) || schemaType.equals(SchemaType.SET)) {

            HollowCollectionTypeDataAccess collectionTypeDataAccess = (HollowCollectionTypeDataAccess) typeDataAccess;
            HollowCollectionSchema collectionSchema = (HollowCollectionSchema) schema;
            String elementType = collectionSchema.getElementType();

            HollowOrdinalIterator it = collectionTypeDataAccess.ordinalIterator(ordinal);
            List<Object> valueList = new ArrayList<>();
            int refOrdinal = it.next();
            while (refOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                Object[] refValues = getAllValues(refOrdinal, elementType, fieldIndex + 1);
                for (Object value : refValues)
                    valueList.add(value);
                refOrdinal = it.next();
            }
            values = new Object[valueList.size()];
            valueList.toArray(values);

        } else if (schemaType.equals(SchemaType.MAP)) {
            // Map type
            HollowMapTypeDataAccess mapTypeDataAccess = (HollowMapTypeDataAccess) typeDataAccess;
            HollowMapSchema mapSchema = (HollowMapSchema) schema;

            // what to iterate on in map
            boolean iterateThroughKeys = fields[fieldIndex].equals("key");
            String keyOrValueType = iterateThroughKeys ? mapSchema.getKeyType() : mapSchema.getValueType();

            HollowMapEntryOrdinalIterator mapEntryIterator = mapTypeDataAccess.ordinalIterator(ordinal);
            List<Object> valueList = new ArrayList<>();
            while (mapEntryIterator.next()) {
                int keyOrValueOrdinal = iterateThroughKeys ? mapEntryIterator.getKey() : mapEntryIterator.getValue();
                Object[] refValues = getAllValues(keyOrValueOrdinal, keyOrValueType, fieldIndex + 1);
                for (Object value : refValues)
                    valueList.add(value);
            }
            values = new Object[valueList.size()];
            valueList.toArray(values);


        } else {
            // Object type
            HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
            HollowObjectTypeDataAccess objectTypeDataAccess = (HollowObjectTypeDataAccess) typeDataAccess;

            if (fieldTypes[fieldIndex].equals(FieldType.REFERENCE)) {
                int refOrdinal = objectTypeDataAccess.readOrdinal(ordinal, fieldPositions[fieldIndex]);
                if (refOrdinal >= 0) {
                    String refType = objectSchema.getReferencedType(fieldPositions[fieldIndex]);
                    return getAllValues(refOrdinal, refType, fieldIndex + 1);
                }
                return new Object[]{};
            } else {
                return new Object[]{readFromObject(objectTypeDataAccess, ordinal, fieldTypes[fieldIndex], fieldPositions[fieldIndex])};
            }
        }
        return values;
    }

    private Object readFromObject(HollowObjectTypeDataAccess objectTypeDataAccess, int ordinal, FieldType fieldType, int fieldPosition) {
        Object value;
        switch (fieldType) {
            case INT:
                value = objectTypeDataAccess.readInt(ordinal, fieldPosition);
                break;
            case LONG:
                value = objectTypeDataAccess.readLong(ordinal, fieldPosition);
                break;
            case DOUBLE:
                value = objectTypeDataAccess.readDouble(ordinal, fieldPosition);
                break;
            case FLOAT:
                value = objectTypeDataAccess.readFloat(ordinal, fieldPosition);
                break;
            case BOOLEAN:
                value = objectTypeDataAccess.readBoolean(ordinal, fieldPosition);
                break;
            case STRING:
                value = objectTypeDataAccess.readString(ordinal, fieldPosition);
                break;
            default:
                throw new IllegalStateException("Invalid field type :" + fieldType + " cannot read values for this type");

        }
        return value;
    }
}
