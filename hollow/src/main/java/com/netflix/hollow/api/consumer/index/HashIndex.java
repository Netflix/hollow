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

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowRecord;
import java.util.Objects;

/**
 * A type safe hash index for indexing non-primary-key data.
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
 * @param <T> the root, select, and result type
 * @param <Q> the query type
 */
public final class HashIndex<T extends HollowRecord, Q> extends HashIndexSelect<T, T, Q> {

    HashIndex(
            HollowConsumer consumer,
            Class<T> rootType,
            Class<Q> matchedFieldsType) {
        super(consumer,
                rootType,
                rootType, "",
                matchedFieldsType);
    }

    HashIndex(
            HollowConsumer consumer,
            Class<T> rootType,
            String fieldPath, Class<Q> matchedFieldType) {
        super(consumer,
                rootType,
                rootType, "",
                fieldPath, matchedFieldType);
    }

    /**
     * Starts the building of a {@link HashIndex}.
     *
     * @param consumer the consumer containing instances of the given root type
     * @param rootType the root type to match and select from
     * @param <T> the root type
     * @return a builder
     */
    public static <T extends HollowRecord> Builder<T> from(HollowConsumer consumer, Class<T> rootType) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(rootType);
        return new Builder<>(consumer, rootType);
    }

    /**
     * The builder of a {@link HashIndex} or a {@link HashIndexSelect}.
     *
     * @param <T> the root type
     */
    public static final class Builder<T extends HollowRecord> {
        final HollowConsumer consumer;
        final Class<T> rootType;

        Builder(HollowConsumer consumer, Class<T> rootType) {
            this.consumer = consumer;
            this.rootType = rootType;
        }

        /**
         * Creates a {@link HashIndex} for matching with field paths and types declared by
         * {@link FieldPath} annotated fields or methods on the given query type.
         *
         * @param queryType the query type
         * @param <Q> the query type
         * @return a {@code HashIndex}
         * @throws IllegalArgumentException if the query type declares one or more invalid field paths
         * or invalid types given resolution of corresponding field paths
         */
        public <Q> HashIndex<T, Q> usingBean(Class<Q> queryType) {
            Objects.requireNonNull(queryType);
            return new HashIndex<>(consumer, rootType, queryType);
        }

        /**
         * Creates a {@link HashIndex} for matching with a single query field path and type.
         *
         * @param queryFieldPath the query field path
         * @param queryFieldType the query type
         * @param <Q> the query type
         * @return a {@code HashIndex}
         * @throws IllegalArgumentException if the query field path is empty or invalid
         * @throws IllegalArgumentException if the query field type is invalid given resolution of the
         * query field path
         */
        public <Q> HashIndex<T, Q> usingPath(String queryFieldPath, Class<Q> queryFieldType) {
            Objects.requireNonNull(queryFieldPath);
            if(queryFieldPath.isEmpty()) {
                throw new IllegalArgumentException("queryFieldPath argument is an empty String");
            }
            Objects.requireNonNull(queryFieldType);
            return new HashIndex<>(consumer, rootType, queryFieldPath, queryFieldType);
        }

        /**
         * Transitions to build a hash index with result selection.
         *
         * @param selectFieldPath the select field path
         * @param selectFieldType the select, and result, field type associated with the
         * resolved select field path
         * @param <S> the select type
         * @return a builder of a {@link HashIndexSelect}
         */
        public <S extends HollowRecord> BuilderWithSelect<T, S> selectField(
                String selectFieldPath, Class<S> selectFieldType) {
            Objects.requireNonNull(selectFieldPath);
            Objects.requireNonNull(selectFieldType);
            return new BuilderWithSelect<>(consumer, rootType, selectFieldPath, selectFieldType);
        }
    }
}
