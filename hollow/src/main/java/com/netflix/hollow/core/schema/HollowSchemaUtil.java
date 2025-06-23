package com.netflix.hollow.core.schema;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;

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
}
