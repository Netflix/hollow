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

import static java.util.stream.Collectors.toList;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.FieldPaths;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.objectmapper.HollowObjectTypeMapper;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An extractor that extracts an argument value from an instance of a holding type for an associated match field
 * path, transforming the value if necessary from a {@code HollowRecord} to an ordinal integer value.
 *
 * @param <Q> query type
 */
final class MatchFieldPathArgumentExtractor<Q> {

    /**
     * A resolver of a field path.
     */
    interface FieldPathResolver {
        /**
         * Resolves a field path to a {@link FieldPaths.FieldPath}.
         */
        FieldPaths.FieldPath<? extends FieldPaths.FieldSegment> resolve(
                HollowDataset hollowDataAccess, String type, String fieldPath);
    }

    final FieldPaths.FieldPath<? extends FieldPaths.FieldSegment> fieldPath;

    final Function<Q, Object> extractor;

    MatchFieldPathArgumentExtractor(
            FieldPaths.FieldPath<? extends FieldPaths.FieldSegment> fieldPath, Function<Q, ?> extractor) {
        this.fieldPath = fieldPath;
        @SuppressWarnings("unchecked")
        Function<Q, Object> erasedResultExtractor = (Function<Q, Object>) extractor;
        this.extractor = erasedResultExtractor;
    }

    Object extract(Q v) {
        return extractor.apply(v);
    }

    static <Q> List<MatchFieldPathArgumentExtractor<Q>> fromHolderClass(
            HollowDataset dataset, Class<?> rootType, Class<Q> holder,
            FieldPathResolver fpResolver) {
        // @@@ Check for duplicates
        // @@@ Cache result for Q, needs to be associated with dataset
        //     and resolving kind (currently implicit to implementation of fpResolver)
        // @@@ Support holder type of Object[] accepting an instance of String[] for field paths
        //     on construction and Object[] on match enabling "reflective" operation if
        //     static beans are not desired

        // Query annotated fields
        Stream<Field> fields = Stream.of(holder.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(FieldPath.class));

        // Query annotated methods (abstract or concrete) that have
        // a return type and no parameter types
        Stream<Method> methods = Stream.of(holder.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(FieldPath.class))
                .filter(m -> m.getReturnType() != void.class)
                .filter(m -> m.getParameterCount() == 0)
                .filter(m -> !m.isSynthetic())
                .filter(m -> !Modifier.isNative(m.getModifiers()));

        return Stream.concat(fields, methods)
                .sorted(Comparator.comparingInt(f -> f.getDeclaredAnnotation(FieldPath.class).order()))
                .map(ae -> {
                    try {
                        if (ae instanceof Field) {
                            return MatchFieldPathArgumentExtractor.<Q>fromField(dataset, rootType, (Field) ae,
                                    fpResolver);
                        } else {
                            return MatchFieldPathArgumentExtractor.<Q>fromMethod(dataset, rootType, (Method) ae,
                                    fpResolver);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(toList());
    }

    static <Q> MatchFieldPathArgumentExtractor<Q> fromField(
            HollowDataset dataset, Class<?> rootType, Field f,
            FieldPathResolver fpResolver)
            throws IllegalAccessException {
        f.setAccessible(true);
        return fromHandle(dataset, rootType, getFieldPath(f), MethodHandles.lookup().unreflectGetter(f),
                fpResolver);
    }

    static <Q> MatchFieldPathArgumentExtractor<Q> fromMethod(
            HollowDataset dataset, Class<?> rootType, Method m,
            FieldPathResolver fpResolver)
            throws IllegalAccessException {
        if (m.getReturnType() == void.class || m.getParameterCount() > 0) {
            throw new IllegalArgumentException(String.format(
                    "A @FieldPath annotated method must have zero parameters and a non-void return type: %s",
                    m.toGenericString()));
        }
        m.setAccessible(true);
        return fromHandle(dataset, rootType, getFieldPath(m), MethodHandles.lookup().unreflect(m),
                fpResolver);
    }

    static <Q> MatchFieldPathArgumentExtractor<Q> fromHandle(
            HollowDataset dataset, Class<?> rootType, String fieldPath, MethodHandle mh,
            FieldPathResolver fpResolver) {
        return fromFunction(dataset, rootType, fieldPath, mh.type().returnType(), getterGenericExtractor(mh),
                fpResolver);
    }

    static <T> MatchFieldPathArgumentExtractor<T> fromPathAndType(
            HollowDataset dataset, Class<?> rootType, String fieldPath, Class<T> type,
            FieldPathResolver fpResolver) {
        return fromFunction(dataset, rootType, fieldPath, type, Function.identity(),
                fpResolver);
    }

    static IllegalArgumentException incompatibleMatchType(
            Class<?> extractorType, String fieldPath,
            HollowObjectSchema.FieldType schemaFieldType) {
        return new IllegalArgumentException(
                String.format("Match type %s incompatible with field path %s resolving to field of value type %s",
                        extractorType.getName(), fieldPath, schemaFieldType));
    }

    static IllegalArgumentException incompatibleMatchType(
            Class<?> extractorType, String fieldPath, String typeName) {
        return new IllegalArgumentException(
                String.format(
                        "Match type %s incompatible with field path %s resolving to field of reference type %s",
                        extractorType.getName(), fieldPath, typeName));
    }

    static <Q, T> MatchFieldPathArgumentExtractor<Q> fromFunction(
            HollowDataset dataset, Class<?> rootType, String fieldPath,
            Class<T> extractorType, Function<Q, T> extractorFunction,
            FieldPathResolver fpResolver) {
        String rootTypeName = HollowObjectTypeMapper.getDefaultTypeName(rootType);
        FieldPaths.FieldPath<? extends FieldPaths.FieldSegment> fp = fpResolver.resolve(dataset, rootTypeName,
                fieldPath);

        // @@@ Method on FieldPath
        FieldPaths.FieldSegment lastSegment = fp.getSegments().get(fp.getSegments().size() - 1);
        HollowObjectSchema.FieldType schemaFieldType;
        if (lastSegment.getEnclosingSchema().getSchemaType() == HollowSchema.SchemaType.OBJECT) {
            FieldPaths.ObjectFieldSegment os = (FieldPaths.ObjectFieldSegment) lastSegment;
            schemaFieldType = os.getType();
        } else {
            schemaFieldType = HollowObjectSchema.FieldType.REFERENCE;
        }

        Function<Q, ?> extractor = extractorFunction;
        switch (schemaFieldType) {
            case BOOLEAN:
                if (extractorType != boolean.class && extractorType != Boolean.class) {
                    throw incompatibleMatchType(extractorType, fieldPath, schemaFieldType);
                }
                break;
            case DOUBLE:
                if (extractorType != double.class && extractorType != Double.class) {
                    throw incompatibleMatchType(extractorType, fieldPath, schemaFieldType);
                }
                break;
            case FLOAT:
                if (extractorType != float.class && extractorType != Float.class) {
                    throw incompatibleMatchType(extractorType, fieldPath, schemaFieldType);
                }
                break;
            case INT:
                if (extractorType == byte.class || extractorType == Byte.class) {
                    @SuppressWarnings("unchecked")
                    Function<Q, Byte> f = (Function<Q, Byte>) extractorFunction;
                    extractor = f.andThen(Byte::intValue);
                    break;
                } else if (extractorType == short.class || extractorType == Short.class) {
                    @SuppressWarnings("unchecked")
                    Function<Q, Short> f = (Function<Q, Short>) extractorFunction;
                    extractor = f.andThen(Short::intValue);
                    break;
                } else if (extractorType == char.class || extractorType == Character.class) {
                    @SuppressWarnings("unchecked")
                    Function<Q, Character> f = (Function<Q, Character>) extractorFunction;
                    extractor = f.andThen(c -> (int) c);
                } else if (extractorType != int.class && extractorType != Integer.class) {
                    throw incompatibleMatchType(extractorType, fieldPath, schemaFieldType);
                }
                break;
            case LONG:
                if (extractorType != long.class && extractorType != Long.class) {
                    throw incompatibleMatchType(extractorType, fieldPath, schemaFieldType);
                }
                break;
            case REFERENCE: {
                // @@@ If extractorType == int.class then consider it an ordinal value
                //   and directly use the extractorFunction

                String typeName = lastSegment.getTypeName();

                // Manage for String and all box types
                if (typeName.equals("String")) {
                    if (!HollowObject.class.isAssignableFrom(extractorType)) {
                        throw incompatibleMatchType(extractorType, fieldPath, typeName);
                    }
                    // @@@ Check that object schema has single value field of String type such as HString
                } else if (!extractorType.getSimpleName().equals(typeName)) {
                    throw incompatibleMatchType(extractorType, fieldPath, typeName);
                } else if (!HollowRecord.class.isAssignableFrom(extractorType)) {
                    throw incompatibleMatchType(extractorType, fieldPath, typeName);
                }

                @SuppressWarnings("unchecked")
                Function<Q, HollowRecord> f = (Function<Q, HollowRecord>) extractorFunction;
                extractor = f.andThen(HollowRecord::getOrdinal);
                break;
            }
            case BYTES:
                if (extractorType != byte[].class) {
                    throw incompatibleMatchType(extractorType, fieldPath, schemaFieldType);
                }
                break;
            case STRING:
                if (extractorType == char[].class) {
                    @SuppressWarnings("unchecked")
                    Function<Q, char[]> f = (Function<Q, char[]>) extractorFunction;
                    extractor = f.andThen(String::valueOf);
                    break;
                } else if (extractorType != String.class) {
                    throw incompatibleMatchType(extractorType, fieldPath, schemaFieldType);
                }
                break;
        }

        return new MatchFieldPathArgumentExtractor<>(fp, extractor);
    }

    private static <Q, T> Function<Q, T> getterGenericExtractor(MethodHandle getter) {
        return h -> {
            try {
                @SuppressWarnings("unchecked")
                T t = (T) getter.invoke(h);
                return t;
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static String getFieldPath(Field f) {
        return getFieldPath(f, f);
    }

    private static String getFieldPath(Method m) {
        return getFieldPath(m, m);
    }

    private static String getFieldPath(Member m, AnnotatedElement e) {
        FieldPath fpa = e.getDeclaredAnnotation(FieldPath.class);
        if (fpa == null) {
            return m.getName();
        }

        String fieldPath = e.getDeclaredAnnotation(FieldPath.class).value();
        if (fieldPath.isEmpty()) {
            return m.getName();
        }

        return fieldPath;
    }
}
