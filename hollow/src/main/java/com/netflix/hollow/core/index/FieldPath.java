package com.netflix.hollow.core.index;

import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is used to represent a field path. A field path is a "." separated list of fields that are used for traversal when looking up values for a type and ordinal of that type.
 * It is used to read field values for a given type and its ordinal. It is convenient, when the path is deeply nested.
 * <p>
 * Upon initializing a instance of this class with given parameters, it does the following checks
 * <ul>
 * <li>valid types in the HollowReadStateEngine</li>
 * <li>auto-expands the path for collections (appending ".element" if missing), and a reference type (e.g String/Integer appending ".value" if missing) with single field. (except for Map schema)</li>
 * <li>and checks that the field path leads to one of the valid field types provided in the constructor</li>
 * <li>for map schema type, specify "key" to iterate over key types and "value" to iterate over value types.</li>
 * </ul>
 * <p>
 * This class also has a convenience method to find values following the field path and given a start field position.
 * This class is meant as a utility class to read deeply nested fields for a given type and its ordinal.
 */
public class FieldPath {

    private final String fieldPath;
    private final HollowReadStateEngine readStateEngine;
    private final String type;
    private final Set<HollowObjectSchema.FieldType> validFieldTypes;

    private String[] fields;
    private int[] fieldPositions;
    private HollowObjectSchema.FieldType[] fieldTypes;
    private String lastRefTypeInPath;

    private boolean autoExpand = true;

    /**
     * Create new FieldPath with auto-expand feature.
     *
     * @param readStateEngine read state engine
     * @param type            parent type of the field path
     * @param fieldPath       "." separated fields
     * @param validFieldType  field path should end with one of the given field type.
     */
    protected FieldPath(HollowReadStateEngine readStateEngine, String type, String fieldPath, HollowObjectSchema.FieldType validFieldType) {
        this(readStateEngine, type, fieldPath, new HashSet<>(Arrays.asList(validFieldType)), true);
    }

    /**
     * Create new FieldPath.
     *
     * @param readStateEngine read state engine
     * @param type            parent type of the field path
     * @param fieldPath       "." separated fields
     * @param validFieldType  field path should end with one of the given field type.
     * @param autoExpand      if the field path should be auto-expand collections and references with one field.
     */
    protected FieldPath(HollowReadStateEngine readStateEngine, String type, String fieldPath, HollowObjectSchema.FieldType validFieldType, boolean autoExpand) {
        this(readStateEngine, type, fieldPath, new HashSet<>(Arrays.asList(validFieldType)), autoExpand);
    }

    private FieldPath(HollowReadStateEngine readStateEngine, String type, String fieldPath, Set<HollowObjectSchema.FieldType> validFieldTypes, boolean autoExpand) {
        this.fieldPath = fieldPath;
        this.readStateEngine = readStateEngine;
        this.type = type;
        this.validFieldTypes = validFieldTypes;
        this.autoExpand = autoExpand;
        initialize();
    }

    private void initialize() {
        String[] fieldParts = fieldPath.split("\\.");
        List<String> fields = new ArrayList<>();
        List<Integer> fieldPositions = new ArrayList<>();
        List<HollowObjectSchema.FieldType> fieldTypes = new ArrayList<>();

        // traverse through the field path to save field position and types.
        String refType = type;
        String lastRefType = type;
        int i = 0;
        while (i < fieldParts.length || refType != null) {

            HollowSchema schema = readStateEngine.getSchema(refType);
            if (schema == null)
                throw new IllegalArgumentException("Schema not found for the type : " + refType);
            HollowSchema.SchemaType schemaType = readStateEngine.getSchema(refType).getSchemaType();

            String fieldName = null;
            int fieldPosition = 0;
            HollowObjectSchema.FieldType fieldType = HollowObjectSchema.FieldType.REFERENCE;

            if (schemaType.equals(HollowSchema.SchemaType.OBJECT)) {

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
                if (fieldType.equals(HollowObjectSchema.FieldType.REFERENCE)) {
                    refType = objectSchema.getReferencedType(fieldName);
                } else if (validFieldTypes.contains(fieldType)) {
                    lastRefType = refType;
                    refType = null;
                } else {
                    throw new IllegalArgumentException("Field path should contain references and should lead to a field of type :" + Arrays.toString(validFieldTypes.toArray()));
                }

            } else if (schemaType.equals(HollowSchema.SchemaType.LIST) || schemaType.equals(HollowSchema.SchemaType.SET)) {

                if (autoExpand && (i >= fieldParts.length || (i < fieldParts.length && !fieldParts[i].equals("element")))) {
                    fieldName = "element";
                } else {
                    fieldName = fieldParts[i];
                    i++; // increment index for field part for next iteration.
                }
                // update ref type to element type.
                refType = ((HollowCollectionSchema) schema).getElementType();

            } else if (schemaType.equals(HollowSchema.SchemaType.MAP)) {

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
        this.fieldTypes = fieldTypes.toArray(new HollowObjectSchema.FieldType[fieldTypes.size()]);
        this.lastRefTypeInPath = lastRefType;

        // field path should ultimately lead down to one of the valid type.
        if (!validFieldTypes.contains(this.fieldTypes[this.fields.length - 1])) {
            throw new IllegalArgumentException("Field path should resolve to the one of the valid types :" + Arrays.toString(validFieldTypes.toArray()));
        }

    }

    protected String getLastRefTypeInPath() {
        return lastRefTypeInPath;
    }

    /**
     * Recursively find all the values following the field path.
     *
     * @param ordinal ordinal record for the given type in field path
     * @return Array of values found at the field path for the given ordinal record in the type.
     */
    protected Object[] findValuesFollowingPath(int ordinal) {
        return findValuesFollowingPath(ordinal, type, 0);
    }


    private Object[] findValuesFollowingPath(int ordinal, String type, int fieldIndex) {
        Object[] values;
        HollowTypeReadState typeReadState = readStateEngine.getTypeState(type);
        HollowSchema.SchemaType schemaType = readStateEngine.getSchema(type).getSchemaType();
        HollowSchema schema = readStateEngine.getSchema(type);

        if (schemaType.equals(HollowSchema.SchemaType.LIST) || schemaType.equals(HollowSchema.SchemaType.SET)) {

            HollowCollectionTypeReadState collectionTypeReadState = (HollowCollectionTypeReadState) typeReadState;
            HollowCollectionSchema collectionSchema = (HollowCollectionSchema) schema;
            String elementType = collectionSchema.getElementType();

            HollowOrdinalIterator it = collectionTypeReadState.ordinalIterator(ordinal);
            List<Object> valueList = new ArrayList<>();
            int refOrdinal = it.next();
            while (refOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                Object[] refValues = findValuesFollowingPath(refOrdinal, elementType, fieldIndex + 1);
                for (Object value : refValues)
                    valueList.add(value);
                refOrdinal = it.next();
            }
            values = new Object[valueList.size()];
            valueList.toArray(values);

        } else if (schemaType.equals(HollowSchema.SchemaType.MAP)) {
            // Map type
            HollowMapTypeReadState mapTypeReadState = (HollowMapTypeReadState) typeReadState;
            HollowMapSchema mapSchema = (HollowMapSchema) schema;

            // what to iterate on in map
            boolean iterateThroughKeys = fields[fieldIndex].equals("key");
            String keyOrValueType = iterateThroughKeys ? mapSchema.getKeyType() : mapSchema.getValueType();

            HollowMapEntryOrdinalIterator mapEntryIterator = mapTypeReadState.ordinalIterator(ordinal);
            List<Object> valueList = new ArrayList<>();
            while (mapEntryIterator.next()) {
                int keyOrValueOrdinal = iterateThroughKeys ? mapEntryIterator.getKey() : mapEntryIterator.getValue();
                Object[] refValues = findValuesFollowingPath(keyOrValueOrdinal, keyOrValueType, fieldIndex + 1);
                for (Object value : refValues)
                    valueList.add(value);
            }
            values = new Object[valueList.size()];
            valueList.toArray(values);


        } else {
            // Object type
            HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
            HollowObjectTypeReadState objectTypeReadState = (HollowObjectTypeReadState) typeReadState;

            if (fieldTypes[fieldIndex].equals(HollowObjectSchema.FieldType.REFERENCE)) {
                int refOrdinal = objectTypeReadState.readOrdinal(ordinal, fieldPositions[fieldIndex]);
                String refType = objectSchema.getReferencedType(fieldPositions[fieldIndex]);
                return findValuesFollowingPath(refOrdinal, refType, fieldIndex + 1);
            } else {
                switch (fieldTypes[fieldIndex]) {
                    case INT:
                        values = new Integer[]{objectTypeReadState.readInt(ordinal, fieldPositions[fieldIndex])};
                        break;
                    case LONG:
                        values = new Long[]{objectTypeReadState.readLong(ordinal, fieldPositions[fieldIndex])};
                        break;
                    case DOUBLE:
                        values = new Double[]{objectTypeReadState.readDouble(ordinal, fieldPositions[fieldIndex])};
                        break;
                    case FLOAT:
                        values = new Float[]{objectTypeReadState.readFloat(ordinal, fieldPositions[fieldIndex])};
                        break;
                    case BOOLEAN:
                        values = new Boolean[]{objectTypeReadState.readBoolean(ordinal, fieldPositions[fieldIndex])};
                        break;
                    case STRING:
                        values = new String[]{objectTypeReadState.readString(ordinal, fieldPositions[fieldIndex])};
                        break;
                    default:
                        throw new IllegalStateException("Invalid field type :" + fieldTypes[fieldIndex] + " cannot read values for this type");

                }
            }
        }
        return values;
    }
}
