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

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.core.index.FieldPaths;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
                        .fromHolderClass(consumer.getStateEngine(), rootType, matchFieldsType,
                                FieldPaths::createFieldPathForHashIndex));
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
                                .fromPathAndType(consumer.getStateEngine(), rootType, fieldPath, matchFieldType,
                                        FieldPaths::createFieldPathForHashIndex)));
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
