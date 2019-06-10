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

import static java.util.stream.Collectors.joining;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.core.index.FieldPaths;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectTypeMapper;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A type safe hash index, with result selection, for indexing non-primary-key data.
 * <p>
 * This type of index can map multiple keys to a single matching record,
 * and/or multiple records to a single key.
 * <p>
 * If the index is {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered} with its
 * associated {@link HollowConsumer} then the index will track updates and changes will be reflected in matched results
 * (performed after such updates).  When a registered index is no longer needed it should be
 * {@link HollowConsumer#removeRefreshListener(HollowConsumer.RefreshListener) deregistered} to avoid unnecessary
 * index recalculation and to ensure the index is reclaimed by the garbage collector.
 *
 * @param <T> the root type
 * @param <S> the select and result type
 * @param <Q> the query type
 */
public class HashIndexSelect<T extends HollowRecord, S extends HollowRecord, Q>
        implements HollowConsumer.RefreshListener, HollowConsumer.RefreshRegistrationListener, Function<Q, Stream<S>> {
    final HollowConsumer consumer;
    HollowAPI api;
    final SelectFieldPathResultExtractor<S> selectField;
    final List<MatchFieldPathArgumentExtractor<Q>> matchFields;
    final String rootTypeName;
    final String selectFieldPath;
    final String[] matchFieldPaths;
    HollowHashIndex hhi;

    HashIndexSelect(
            HollowConsumer consumer,
            Class<T> rootType,
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
        this.rootTypeName = HollowObjectTypeMapper.getDefaultTypeName(rootType);

        this.hhi = new HollowHashIndex(consumer.getStateEngine(), rootTypeName, selectFieldPath, matchFieldPaths);
    }

    HashIndexSelect(
            HollowConsumer consumer,
            Class<T> rootType,
            Class<S> selectType, String selectField,
            Class<Q> matchFieldsType) {
        this(consumer,
                rootType,
                SelectFieldPathResultExtractor
                        .from(consumer.getAPI().getClass(), consumer.getStateEngine(), rootType, selectField,
                                selectType),
                MatchFieldPathArgumentExtractor
                        .fromHolderClass(consumer.getStateEngine(), rootType, matchFieldsType,
                                FieldPaths::createFieldPathForHashIndex));
    }

    HashIndexSelect(
            HollowConsumer consumer,
            Class<T> rootType,
            Class<S> selectType, String selectField,
            String fieldPath, Class<Q> matchFieldType) {
        this(consumer,
                rootType,
                SelectFieldPathResultExtractor
                        .from(consumer.getAPI().getClass(), consumer.getStateEngine(), rootType, selectField,
                                selectType),
                Collections.singletonList(
                        MatchFieldPathArgumentExtractor
                                .fromPathAndType(consumer.getStateEngine(), rootType, fieldPath, matchFieldType,
                                        FieldPaths::createFieldPathForHashIndex)));
    }

    /**
     * Finds matches for a given query.
     *
     * @param query the query
     * @return a stream of matching records (may be empty if there are no matches)
     */
    @Override
    public Stream<S> apply(Q query) {
        return findMatches(query);
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

    // HollowConsumer.RefreshListener

    @Override public void refreshStarted(long currentVersion, long requestedVersion) {
    }

    @Override public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) {
        HollowHashIndex hhi = this.hhi;
        hhi.detachFromDeltaUpdates();
        hhi = new HollowHashIndex(consumer.getStateEngine(), rootTypeName, selectFieldPath, matchFieldPaths);
        hhi.listenForDeltaUpdates();
        this.hhi = hhi;
        this.api = api;
    }

    @Override public void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) {
        this.api = api;
    }

    @Override public void blobLoaded(HollowConsumer.Blob transition) {
    }

    @Override public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {
    }

    @Override public void refreshFailed(
            long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {
    }

    // HollowConsumer.RefreshRegistrationListener

    @Override public void onBeforeAddition(HollowConsumer c) {
        if (c != consumer) {
            throw new IllegalStateException("The index's consumer and the listener's consumer are not the same");
        }
        hhi.listenForDeltaUpdates();
    }

    @Override public void onAfterRemoval(HollowConsumer c) {
        hhi.detachFromDeltaUpdates();
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
        final String selectFieldPath;
        final Class<S> selectFieldType;

        BuilderWithSelect(
                HollowConsumer consumer, Class<T> rootType,
                String selectFieldPath, Class<S> selectFieldType) {
            this.consumer = consumer;
            this.rootType = rootType;
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
            return new HashIndexSelect<>(consumer, rootType, selectFieldType, selectFieldPath,
                    queryType);
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
            return new HashIndexSelect<>(consumer, rootType, selectFieldType, selectFieldPath,
                    queryFieldPath, queryFieldType);
        }
    }
}
