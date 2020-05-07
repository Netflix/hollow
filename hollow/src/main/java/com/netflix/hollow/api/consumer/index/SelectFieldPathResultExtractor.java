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
package com.netflix.hollow.api.consumer.index;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.FieldPaths;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.objectmapper.HollowObjectTypeMapper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * An extractor that extracts a result value for an associated select field path transforming the value if
 * necessary from an ordinal integer to an instance of a {@code HollowRecord} depending on the result type.
 *
 * @param <T> the result type
 */
final class SelectFieldPathResultExtractor<T> {
    final FieldPaths.FieldPath<FieldPaths.FieldSegment> fieldPath;

    final BiObjectIntFunction<HollowAPI, T> extractor;

    SelectFieldPathResultExtractor(
            FieldPaths.FieldPath<FieldPaths.FieldSegment> fieldPath,
            BiObjectIntFunction<HollowAPI, T> extractor) {
        this.fieldPath = fieldPath;
        this.extractor = extractor;
    }

    interface BiObjectIntFunction<T, R> {
        R apply(T t, int i);
    }

    T extract(HollowAPI api, int ordinal) {
        return extractor.apply(api, ordinal);
    }

    static IllegalArgumentException incompatibleSelectType(
            Class<?> selectType, String fieldPath, HollowObjectSchema.FieldType schemaFieldType) {
        return new IllegalArgumentException(
                String.format("Select type %s incompatible with field path %s resolving to field of type %s",
                        selectType.getName(), fieldPath, schemaFieldType));
    }

    static IllegalArgumentException incompatibleSelectType(Class<?> selectType, String fieldPath, String typeName) {
        return new IllegalArgumentException(
                String.format(
                        "Select type %s incompatible with field path %s resolving to field of reference type %s",
                        selectType.getName(), fieldPath, typeName));
    }

    static <T> SelectFieldPathResultExtractor<T> from(
            Class<? extends HollowAPI> apiType, HollowDataset dataset, Class<?> rootType, String fieldPath,
            Class<T> selectType) {
        String rootTypeName = HollowObjectTypeMapper.getDefaultTypeName(rootType);
        FieldPaths.FieldPath<FieldPaths.FieldSegment> fp =
                FieldPaths.createFieldPathForHashIndex(dataset, rootTypeName, fieldPath);

        String typeName;
        if (!fp.getSegments().isEmpty()) {
            // @@@ Method on FieldPath
            FieldPaths.FieldSegment lastSegment = fp.getSegments().get(fp.getSegments().size() - 1);
            HollowSchema.SchemaType schemaType = lastSegment.getEnclosingSchema().getSchemaType();
            HollowObjectSchema.FieldType schemaFieldType;
            if (schemaType == HollowSchema.SchemaType.OBJECT) {
                FieldPaths.ObjectFieldSegment os = (FieldPaths.ObjectFieldSegment) lastSegment;
                schemaFieldType = os.getType();
            } else {
                schemaFieldType = HollowObjectSchema.FieldType.REFERENCE;
            }
            typeName = lastSegment.getTypeName();

            if (schemaFieldType != HollowObjectSchema.FieldType.REFERENCE) {
                // The field path must reference a field of a reference type
                // This is contrary to the underlying HollowHashIndex which selects
                // the enclosing reference type for a field of a value type.
                // It is considered better to be consistent and literal with field path
                // expressions
                throw incompatibleSelectType(selectType, fieldPath, schemaFieldType);
            } else if (typeName.equals("String")) {
                if (!HollowObject.class.isAssignableFrom(selectType)) {
                    throw incompatibleSelectType(selectType, fieldPath, typeName);
                }
                // @@@ Check that object schema has single value field of String type such as HString
            } else if (!HollowObjectTypeMapper.getDefaultTypeName(selectType).equals(typeName)) {
                if (schemaType != HollowSchema.SchemaType.OBJECT && !GenericHollowObject.class.isAssignableFrom(
                        selectType)) {
                    throw incompatibleSelectType(selectType, fieldPath, typeName);
                }
                // @@@ GenericHollow{List, Set, Map} based on schemaType
            } else if (!HollowRecord.class.isAssignableFrom(selectType)) {
                throw incompatibleSelectType(selectType, fieldPath, typeName);
            }
        } else {
            typeName = rootTypeName;
        }

        if (GenericHollowObject.class.isAssignableFrom(selectType)) {
            BiObjectIntFunction<HollowAPI, T> extractor =
                    (a, o) -> {
                        @SuppressWarnings("unchecked")
                        T t = (T) new GenericHollowObject(a.getDataAccess(), typeName, o);
                        return t;
                    };
            return new SelectFieldPathResultExtractor<>(fp, extractor);
        } else {
            MethodHandle selectInstantiate;
            try {
                selectInstantiate = MethodHandles.lookup().findVirtual(
                        apiType,
                        "deserializeFrom" + selectType.getSimpleName(),
                        MethodType.methodType(selectType, int.class));
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new IllegalArgumentException(
                        String.format("Select type %s is not associated with API %s",
                                selectType.getName(), apiType.getName()),
                        e);
            }

            BiObjectIntFunction<HollowAPI, T> extractor = (a, i) -> {
                try {
                    @SuppressWarnings("unchecked")
                    T s = (T) selectInstantiate.invoke(a, i);
                    return s;
                } catch (RuntimeException | Error e) {
                    throw e;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };

            return new SelectFieldPathResultExtractor<>(fp, extractor);
        }
    }
}
