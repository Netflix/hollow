package com.netflix.hollow.core.schema;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HollowSchemaUtil {

    /* *
     * Finds the top-level types in the Hollow dataset. A top-level type is one that is not a
     * reference from another type, i.e., it is not an element of a list, set, or map, nor is it
     * a field in an object schema.
     *
     * @return a set of top-level type names
     */
    public static Set<String> getTopLevelTypes(HollowReadStateEngine readState) {
        List<HollowSchema> schemas = readState.getSchemas();
        Set<String> topLevelTypes = new HashSet<>(readState.getAllTypes());
        for (HollowSchema schema : schemas) {
            switch (schema.getSchemaType()) {
                case LIST:
                    HollowListSchema listSchema = (HollowListSchema) schema;
                    topLevelTypes.remove(listSchema.getElementType());
                    break;
                case SET:
                    HollowSetSchema setSchema = (HollowSetSchema) schema;
                    topLevelTypes.remove(setSchema.getElementType());
                    break;
                case MAP:
                    HollowMapSchema mapSchema = (HollowMapSchema) schema;
                    topLevelTypes.remove(mapSchema.getKeyType());
                    topLevelTypes.remove(mapSchema.getValueType());
                    break;
                case OBJECT:
                    HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
                    for (int fieldIdx=0; fieldIdx < objectSchema.numFields(); fieldIdx++) {
                        if (objectSchema.getFieldType(fieldIdx) == HollowObjectSchema.FieldType.REFERENCE) {
                            topLevelTypes.remove(objectSchema.getReferencedType(fieldIdx));
                        }
                    }
            }
        }
        return topLevelTypes;
    }

    /**
     * Get the names of all types referenced by a schema (via REFERENCE fields,
     * collection element types, or map key/value types).
     *
     * @param schema the schema to inspect
     * @return list of referenced type names
     */
    public static List<String> getReferencedTypeNames(HollowSchema schema) {
        List<String> refs = new ArrayList<>();
        if (schema instanceof HollowObjectSchema) {
            HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
            for (int i = 0; i < objectSchema.numFields(); i++) {
                if (objectSchema.getFieldType(i) == HollowObjectSchema.FieldType.REFERENCE) {
                    refs.add(objectSchema.getReferencedType(i));
                }
            }
        } else if (schema instanceof HollowCollectionSchema) {
            refs.add(((HollowCollectionSchema) schema).getElementType());
        } else if (schema instanceof HollowMapSchema) {
            HollowMapSchema mapSchema = (HollowMapSchema) schema;
            refs.add(mapSchema.getKeyType());
            refs.add(mapSchema.getValueType());
        }
        return refs;
    }

    /**
     * Describes the first difference that prevents records serialized with {@code other} from being read as
     * {@code schema}, or returns null if there is none. Primary keys and hash keys are ignored.
     *
     * @param schema the schema records are read with
     * @param other the schema records are serialized with
     * @return the first difference, or null if the layouts match
     */
    public static String findLayoutDifference(HollowSchema schema, HollowSchema other) {
        if (schema.getSchemaType() != other.getSchemaType())
            return "schema type (" + schema.getSchemaType() + " vs " + other.getSchemaType() + ")";

        switch (schema.getSchemaType()) {
            case OBJECT:
                HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
                HollowObjectSchema otherObjectSchema = (HollowObjectSchema) other;
                for (int i = 0; i < Math.max(objectSchema.numFields(), otherObjectSchema.numFields()); i++) {
                    if (!sameField(objectSchema, otherObjectSchema, i))
                        return "field " + i + " (" + describeField(objectSchema, i) + " vs " + describeField(otherObjectSchema, i) + ")";
                }
                return null;
            case LIST:
            case SET:
                return typeDifference("element type",
                        ((HollowCollectionSchema) schema).getElementType(), ((HollowCollectionSchema) other).getElementType());
            case MAP:
                HollowMapSchema mapSchema = (HollowMapSchema) schema;
                HollowMapSchema otherMapSchema = (HollowMapSchema) other;
                String keyDifference = typeDifference("key type", mapSchema.getKeyType(), otherMapSchema.getKeyType());
                return keyDifference != null ? keyDifference
                        : typeDifference("value type", mapSchema.getValueType(), otherMapSchema.getValueType());
            default:
                return "unsupported schema type (" + schema.getSchemaType() + ")";
        }
    }

    private static boolean sameField(HollowObjectSchema schema, HollowObjectSchema other, int fieldIndex) {
        if (fieldIndex >= schema.numFields() || fieldIndex >= other.numFields())
            return false;
        HollowObjectSchema.FieldType fieldType = schema.getFieldType(fieldIndex);
        return fieldType == other.getFieldType(fieldIndex)
                && schema.getFieldName(fieldIndex).equals(other.getFieldName(fieldIndex))
                && (fieldType != HollowObjectSchema.FieldType.REFERENCE
                        || schema.getReferencedType(fieldIndex).equals(other.getReferencedType(fieldIndex)));
    }

    private static String describeField(HollowObjectSchema schema, int fieldIndex) {
        if (fieldIndex >= schema.numFields())
            return "none";
        HollowObjectSchema.FieldType fieldType = schema.getFieldType(fieldIndex);
        return schema.getFieldName(fieldIndex) + " " + (fieldType == HollowObjectSchema.FieldType.REFERENCE
                ? "REFERENCE(" + schema.getReferencedType(fieldIndex) + ")"
                : fieldType.toString());
    }

    private static String typeDifference(String label, String type, String otherType) {
        return type.equals(otherType) ? null : label + " (" + type + " vs " + otherType + ")";
    }
}
