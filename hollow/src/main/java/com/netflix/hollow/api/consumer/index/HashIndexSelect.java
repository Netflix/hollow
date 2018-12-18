/*
 *
 *  Copyright 2019 Netflix, Inc.
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

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.FieldPaths;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A type safe hash index, with result selection, for indexing non-primary-key data.
 * <p>
 * This type of index can map multiple keys to a single matching record,
 * and/or multiple records to a single key.
 *
 * @param <T> the root type
 * @param <S> the select and result type
 * @param <Q> the query type
 */
public class HashIndexSelect<T extends HollowRecord, S extends HollowRecord, Q> {
    final HollowConsumer consumer;
    HollowAPI api;
    boolean listenToDataRefresh;
    final SelectFieldPathResultExtractor<S> selectField;
    final List<MatchFieldPathArgumentExtractor<Q>> matchFields;
    final String rootTypeName;
    final String selectFieldPath;
    final String[] matchFieldPaths;
    HollowHashIndex hhi;
    RefreshListener refreshListener;

    HashIndexSelect(
            HollowConsumer consumer,
            Class<T> rootType,
            boolean listenToDataRefresh,
            SelectFieldPathResultExtractor<S> selectField,
            List<MatchFieldPathArgumentExtractor<Q>> matchFields) {
        this.consumer = consumer;
        this.api = consumer.getAPI();
        this.selectField = selectField;
        this.matchFields = matchFields;

        // Validate select field path
        // @@@ Add method to FieldPath
        this.selectFieldPath = selectField.fieldPath.getSegments().stream().map(FieldPaths.FieldSegment::getName)
                .collect(joining("."));

        // Validate match field paths
        this.matchFieldPaths = matchFields.stream()
                // @@@ Add method to FieldPath
                .map(mf -> mf.fieldPath.getSegments().stream().map(FieldPaths.FieldSegment::getName)
                        .collect(joining(".")))
                .toArray(String[]::new);
        this.rootTypeName = rootType.getSimpleName();

        this.hhi = new HollowHashIndex(consumer.getStateEngine(), rootTypeName, selectFieldPath, matchFieldPaths);

        if (listenToDataRefresh) {
            listenToDataRefresh();
        }
    }

    HashIndexSelect(
            HollowConsumer consumer,
            Class<T> rootType,
            boolean listenToDataRefresh,
            Class<S> selectType, String selectField,
            Class<Q> matchFieldsType) {
        this(consumer,
                rootType,
                listenToDataRefresh,
                SelectFieldPathResultExtractor
                        .from(consumer.getAPI().getClass(), consumer.getStateEngine(), rootType, selectField,
                                selectType),
                MatchFieldPathArgumentExtractor
                        .fromHolderClass(consumer.getStateEngine(), rootType, matchFieldsType));
    }

    HashIndexSelect(
            HollowConsumer consumer,
            Class<T> rootType,
            boolean listenToDataRefresh,
            Class<S> selectType, String selectField,
            String fieldPath, Class<Q> matchFieldType) {
        this(consumer,
                rootType,
                listenToDataRefresh,
                SelectFieldPathResultExtractor
                        .from(consumer.getAPI().getClass(), consumer.getStateEngine(), rootType, selectField,
                                selectType),
                Collections.singletonList(
                        MatchFieldPathArgumentExtractor
                                .fromPathAndType(consumer.getStateEngine(), rootType, fieldPath, matchFieldType)));
    }

    /**
     * Listens to {@code HollowConsumer} version updates.
     * On an update the index recalculates so updated data will be reflected in the results of a query
     * (performed after the update).
     */
    public void listenToDataRefresh() {
        if (listenToDataRefresh) {
            return;
        }
        listenToDataRefresh = true;

        if (refreshListener == null) {
            refreshListener = new RefreshListener();
        }

        hhi.listenForDeltaUpdates();
        consumer.addRefreshListener(refreshListener);
    }

    /**
     * Disables listening to {@code HollowConsumer} version updates.
     * Updated data will not be reflected in the results of a query.
     * <p>
     * This method should be called before the index is discarded to ensure unnecessary recalculation
     * is not performed and to ensure the index is reclaimed by the garbage collector.
     */
    public void detachFromDataRefresh() {
        if (!listenToDataRefresh) {
            return;
        }
        listenToDataRefresh = false;

        hhi.detachFromDeltaUpdates();
        consumer.removeRefreshListener(refreshListener);
    }

    final class RefreshListener implements HollowConsumer.RefreshListener {
        @Override
        public void snapshotUpdateOccurred(HollowAPI refreshAPI, HollowReadStateEngine stateEngine, long version) {
            hhi.detachFromDeltaUpdates();
            hhi = new HollowHashIndex(consumer.getStateEngine(), rootTypeName, selectFieldPath, matchFieldPaths);
            hhi.listenForDeltaUpdates();
            api = refreshAPI;
        }

        @Override
        public void deltaUpdateOccurred(HollowAPI refreshAPI, HollowReadStateEngine stateEngine, long version) {
            api = refreshAPI;
        }

        @Override public void refreshStarted(long currentVersion, long requestedVersion) {
        }

        @Override public void blobLoaded(HollowConsumer.Blob transition) {
        }

        @Override public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {
        }

        @Override public void refreshFailed(
                long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {
        }
    }

    /**
     * Finds matches for a given query.
     *
     * @param query the query
     * @return a stream of matching records (may be empty if there are no matches)
     */
    public Stream<S> findMatches(Q query) {
        Object[] queryArray = matchFields.stream().map(mf -> mf.extract(query)).toArray();

        HollowHashIndexResult matches = hhi.findMatches(queryArray);
        if (matches == null) {
            return Stream.empty();
        }

        return matches.stream().mapToObj(i -> selectField.extract(api, i));
    }

    /**
     * An extractor that extracts an argument value from an instance of a holding type for an associated match field
     * path, transforming the value if necessary from a {@code HollowRecord} to an ordinal integer value.
     *
     * @param <Q> query type
     */
    static final class MatchFieldPathArgumentExtractor<Q> {
        final FieldPaths.FieldPath<FieldPaths.FieldSegment> fieldPath;

        final Function<Q, Object> extractor;

        MatchFieldPathArgumentExtractor(
                FieldPaths.FieldPath<FieldPaths.FieldSegment> fieldPath, Function<Q, ?> extractor) {
            this.fieldPath = fieldPath;
            @SuppressWarnings("unchecked")
            Function<Q, Object> erasedResultExtractor = (Function<Q, Object>) extractor;
            this.extractor = erasedResultExtractor;
        }

        Object extract(Q v) {
            return extractor.apply(v);
        }

        static <Q> List<MatchFieldPathArgumentExtractor<Q>> fromHolderClass(
                HollowDataset dataset, Class<?> rootType, Class<Q> holder) {
            // @@@ Check for duplicates

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
                                return MatchFieldPathArgumentExtractor.<Q>fromField(dataset, rootType, (Field) ae);
                            } else {
                                return MatchFieldPathArgumentExtractor.<Q>fromMethod(dataset, rootType, (Method) ae);

                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(toList());
        }

        static <Q> MatchFieldPathArgumentExtractor<Q> fromField(HollowDataset dataset, Class<?> rootType, Field f)
                throws IllegalAccessException {
            f.setAccessible(true);
            return fromHandle(dataset, rootType, getFieldPath(f), MethodHandles.lookup().unreflectGetter(f));
        }

        static <Q> MatchFieldPathArgumentExtractor<Q> fromMethod(HollowDataset dataset, Class<?> rootType, Method m)
                throws IllegalAccessException {
            if (m.getReturnType() == void.class || m.getParameterCount() > 0) {
                throw new IllegalArgumentException(String.format(
                        "A @FieldPath annotated method must have zero parameters and a non-void return type: %s",
                        m.toGenericString()));
            }
            m.setAccessible(true);
            return fromHandle(dataset, rootType, getFieldPath(m), MethodHandles.lookup().unreflect(m));
        }

        static <Q> MatchFieldPathArgumentExtractor<Q> fromHandle(
                HollowDataset dataset, Class<?> rootType, String fieldPath, MethodHandle mh) {
            return fromFunction(dataset, rootType, fieldPath, mh.type().returnType(), getterGenericExtractor(mh));
        }

        static <T> MatchFieldPathArgumentExtractor<T> fromPathAndType(
                HollowDataset dataset, Class<?> rootType, String fieldPath, Class<T> type) {
            return fromFunction(dataset, rootType, fieldPath, type, Function.identity());
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
                Class<T> extractorType, Function<Q, T> extractorFunction) {
            String rootTypeName = rootType.getSimpleName();
            FieldPaths.FieldPath<FieldPaths.FieldSegment> fp =
                    FieldPaths.createFieldPathForHashIndex(dataset, rootTypeName, fieldPath);

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

    /**
     * An extractor that extracts a result value for an associated select field path transforming the value if
     * necessary from an ordinal integer to an instance of a {@code HollowRecord} depending on the result type.
     *
     * @param <T> the result type
     */
    // The select path will be utilized to choose the nearest reference type to the leaf
    // i.e. if the last segment refers to a primitive value then the enclosing schema will be used
    static final class SelectFieldPathResultExtractor<T> {
        final FieldPaths.FieldPath<FieldPaths.FieldSegment> fieldPath;

        final SelectFieldPathResultExtractor.BiObjectIntFunction<HollowAPI, T> extractor;

        SelectFieldPathResultExtractor(
                FieldPaths.FieldPath<FieldPaths.FieldSegment> fieldPath,
                SelectFieldPathResultExtractor.BiObjectIntFunction<HollowAPI, T> extractor) {
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
            String rootTypeName = rootType.getSimpleName();
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
                } else if (!selectType.getSimpleName().equals(typeName)) {
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
                SelectFieldPathResultExtractor.BiObjectIntFunction<HollowAPI, T> extractor =
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
                            "get" + selectType.getSimpleName(),
                            MethodType.methodType(selectType, int.class));
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    throw new IllegalArgumentException(
                            String.format("Select type %s is not associated with API %s",
                                    selectType.getName(), apiType.getName()),
                            e);
                }

                SelectFieldPathResultExtractor.BiObjectIntFunction<HollowAPI, T> extractor = (a, i) -> {
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

    /**
     * The builder of a {@link HashIndexSelect}.
     *
     * @param <T> the root type
     * @param <S> the select, and result, type
     */
    public static class BuilderWithSelect<T extends HollowRecord, S extends HollowRecord> {
        final HollowConsumer consumer;
        final Class<T> rootType;
        final boolean listenToDataRefresh;
        final String selectFieldPath;
        final Class<S> selectFieldType;

        BuilderWithSelect(
                HollowConsumer consumer, Class<T> rootType,
                boolean listenToDataRefresh,
                String selectFieldPath, Class<S> selectFieldType) {
            this.consumer = consumer;
            this.rootType = rootType;
            this.listenToDataRefresh = listenToDataRefresh;
            this.selectFieldPath = selectFieldPath;
            this.selectFieldType = selectFieldType;
        }

        /**
         * Creates a {@link HashIndexSelect} for matching with field paths and types declared by
         * {@link FieldPath} annotated fields or methods on the given query type.
         *
         * @param queryType the query type
         * @param <Q> the query type
         * @return a {@code HashIndexSelect}
         * @throws IllegalArgumentException if the query type declares one or more invalid field paths
         * or invalid types given resolution of corresponding field paths
         * @throws IllegalArgumentException if the select field path is invalid, or the select field type
         * is invalid given resolution of the select field path.
         */
        public <Q> HashIndexSelect<T, S, Q> usingBean(Class<Q> queryType) {
            Objects.requireNonNull(queryType);
            return new HashIndexSelect<>(consumer, rootType, listenToDataRefresh,
                    selectFieldType, selectFieldPath, queryType);
        }

        /**
         * Creates a {@link HashIndexSelect} for matching with a single query field path and type.
         *
         * @param queryFieldPath the query field path
         * @param queryFieldType the query type
         * @param <Q> the query type
         * @return a {@code HashIndexSelect}
         * @throws IllegalArgumentException if the query field path is empty or invalid
         * @throws IllegalArgumentException if the query field type is invalid given resolution of the
         * query field path
         * @throws IllegalArgumentException if the select field path is invalid, or the select field type
         * is invalid given resolution of the select field path.
         */
        public <Q> HashIndexSelect<T, S, Q> usingPath(String queryFieldPath, Class<Q> queryFieldType) {
            Objects.requireNonNull(queryFieldPath);
            if (queryFieldPath.isEmpty()) {
                throw new IllegalArgumentException("selectFieldPath argument is an empty String");
            }
            Objects.requireNonNull(queryFieldType);
            return new HashIndexSelect<>(consumer, rootType, listenToDataRefresh,
                    selectFieldType, selectFieldPath, queryFieldPath, queryFieldType);
        }
    }
}
