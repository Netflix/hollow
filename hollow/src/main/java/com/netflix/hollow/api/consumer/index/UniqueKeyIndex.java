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

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.index.FieldPaths;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.objectmapper.HollowObjectTypeMapper;
import com.netflix.hollow.core.write.objectmapper.HollowTypeMapper;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A type safe index for indexing with a unique key (such as a primary key).
 * <p>
 * If the index is {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered} with its
 * associated {@link HollowConsumer} then the index will track updates and changes will be reflected in matched results
 * (performed after such updates).  When a registered index is no longer needed it should be
 * {@link HollowConsumer#removeRefreshListener(HollowConsumer.RefreshListener) deregistered} to avoid unnecessary
 * index recalculation and to ensure the index is reclaimed by the garbage collector.
 *
 * @param <T> the unique type
 * @param <Q> the key type
 */
public class UniqueKeyIndex<T extends HollowObject, Q>
        implements HollowConsumer.RefreshListener, HollowConsumer.RefreshRegistrationListener, Function<Q, T> {
    final HollowConsumer consumer;
    HollowAPI api;
    final SelectFieldPathResultExtractor<T> uniqueTypeExtractor;
    final List<MatchFieldPathArgumentExtractor<Q>> matchFields;
    final String uniqueSchemaName;
    final String[] matchFieldPaths;
    HollowPrimaryKeyIndex hpki;

    UniqueKeyIndex(
            HollowConsumer consumer,
            Class<T> uniqueType,
            PrimaryKey primaryTypeKey,
            List<MatchFieldPathArgumentExtractor<Q>> matchFields) {
        this.consumer = consumer;
        this.api = consumer.getAPI();
        this.uniqueSchemaName = HollowObjectTypeMapper.getDefaultTypeName(uniqueType);

        this.uniqueTypeExtractor = SelectFieldPathResultExtractor
                .from(consumer.getAPI().getClass(), consumer.getStateEngine(), uniqueType, "", uniqueType);

        if (primaryTypeKey != null) {
            matchFields = validatePrimaryKeyFieldPaths(consumer, uniqueSchemaName, primaryTypeKey, matchFields);
        }

        this.matchFields = matchFields;
        this.matchFieldPaths = matchFields.stream()
                .map(mf -> mf.fieldPath.toString())
                .toArray(String[]::new);

        this.hpki = new HollowPrimaryKeyIndex(consumer.getStateEngine(), uniqueSchemaName, matchFieldPaths);
    }

    static <Q> List<MatchFieldPathArgumentExtractor<Q>> validatePrimaryKeyFieldPaths(
            HollowConsumer consumer, String primaryTypeName,
            PrimaryKey primaryTypeKey, List<MatchFieldPathArgumentExtractor<Q>> matchFields) {

        // Validate primary key field paths
        List<FieldPaths.FieldPath<FieldPaths.ObjectFieldSegment>> paths = Stream.of(
                primaryTypeKey.getFieldPaths())
                .map(fp -> FieldPaths
                        .createFieldPathForPrimaryKey(consumer.getStateEngine(), primaryTypeName, fp))
                .collect(toList());

        // Validate that primary key field paths are the same as that on the match fields
        // If so then match field extractors are shuffled to have the same order as primary key field paths

        List<MatchFieldPathArgumentExtractor<Q>> orderedMatchFields = paths.stream().flatMap(
                path -> {
                    MatchFieldPathArgumentExtractor<Q> mfe =
                            matchFields.stream().filter(e -> e.fieldPath.equals(path)).findFirst().orElse(null);
                    return mfe != null ? Stream.of(mfe) : null;
                }).collect(toList());

        if (orderedMatchFields.size() != paths.size()) {
            // @@@
            throw new IllegalArgumentException();
        }
        return orderedMatchFields;
    }

    UniqueKeyIndex(
            HollowConsumer consumer,
            Class<T> uniqueType,
            PrimaryKey primaryTypeKey,
            Class<Q> matchFieldsType) {
        // @@@ Use FieldPaths.createFieldPathForPrimaryKey
        this(consumer,
                uniqueType,
                primaryTypeKey,
                MatchFieldPathArgumentExtractor
                        .fromHolderClass(consumer.getStateEngine(), uniqueType, matchFieldsType,
                                FieldPaths::createFieldPathForPrimaryKey));
    }

    UniqueKeyIndex(
            HollowConsumer consumer,
            Class<T> uniqueType,
            PrimaryKey primaryTypeKey,
            String fieldPath, Class<Q> matchFieldType) {
        // @@@ Use FieldPaths.createFieldPathForPrimaryKey
        this(consumer,
                uniqueType,
                primaryTypeKey,
                Collections.singletonList(
                        MatchFieldPathArgumentExtractor
                                .fromPathAndType(consumer.getStateEngine(), uniqueType, fieldPath, matchFieldType,
                                        FieldPaths::createFieldPathForPrimaryKey)));
    }


    /**
     * Finds the unique object, an instance of the unique type, for a given key.
     *
     * @param key the key
     * @return the unique object
     */
    @Override
    public T apply(Q key) {
        return findMatch(key);
    }

    /**
     * Finds the unique object, an instance of the unique type, for a given key.
     *
     * @param key the key
     * @return the unique object
     */
    public T findMatch(Q key) {
        Object[] keyArray = matchFields.stream().map(mf -> mf.extract(key)).toArray();

        int ordinal = hpki.getMatchingOrdinal(keyArray);
        if (ordinal == -1) {
            return null;
        }

        return uniqueTypeExtractor.extract(api, ordinal);
    }

    // HollowConsumer.RefreshListener

    @Override public void refreshStarted(long currentVersion, long requestedVersion) {
    }

    @Override public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) {
        HollowPrimaryKeyIndex hpki = this.hpki;
        hpki.detachFromDeltaUpdates();
        hpki = new HollowPrimaryKeyIndex(consumer.getStateEngine(), hpki.getPrimaryKey());
        hpki.listenForDeltaUpdates();
        this.hpki = hpki;
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
        hpki.listenForDeltaUpdates();
    }

    @Override public void onAfterRemoval(HollowConsumer c) {
        hpki.detachFromDeltaUpdates();
    }

    /**
     * Starts the building of a {@link UniqueKeyIndex}.
     *
     * @param consumer the consumer containing instances of the given unique type
     * @param uniqueType the unique type
     * @param <T> the unique type
     * @return a builder
     */
    public static <T extends HollowObject> Builder<T> from(HollowConsumer consumer, Class<T> uniqueType) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(uniqueType);
        return new Builder<>(consumer, uniqueType);
    }

    /**
     * The builder of a {@link UniqueKeyIndex}.
     *
     * @param <T> the unique type
     */
    public static final class Builder<T extends HollowObject> {
        final HollowConsumer consumer;
        final Class<T> uniqueType;
        PrimaryKey primaryTypeKey; // non-null on bindWithPrimaryKeyOnType

        Builder(HollowConsumer consumer, Class<T> uniqueType) {
            this.consumer = consumer;
            this.uniqueType = uniqueType;
        }

        /**
         * Binds the field paths with those of the primary key associated with the schema of the unique type.
         *
         * @throws com.netflix.hollow.api.error.SchemaNotFoundException if there is no schema for the unique
         * type
         * @throws IllegalArgumentException if there is no primary key associated with the unique type
         */
        public Builder<T> bindToPrimaryKey() {
            String primaryTypeName = HollowTypeMapper.getDefaultTypeName(uniqueType);
            HollowSchema schema = consumer.getStateEngine().getNonNullSchema(primaryTypeName);

            assert schema.getSchemaType() == HollowSchema.SchemaType.OBJECT;

            this.primaryTypeKey = ((HollowObjectSchema) schema).getPrimaryKey();
            if (primaryTypeKey == null) {
                throw new IllegalArgumentException(
                        String.format("No primary key associated with primary type %s", uniqueType));
            }
            return this;
        }

        /**
         * Creates a {@link UniqueKeyIndex} for matching with field paths and types declared by
         * {@link FieldPath} annotated fields or methods on the given key type.
         *
         * @param keyType the key type
         * @param <Q> the key type
         * @return a {@code UniqueKeyIndex}
         * @throws IllegalArgumentException if the key type declares one or more invalid field paths
         * or invalid types given resolution of corresponding field paths
         * @throws IllegalArgumentException if the builder is bound to the primary key of the unique type and
         * the field paths declared by the key type are not the identical to those declared by the primary key
         */
        public <Q> UniqueKeyIndex<T, Q> usingBean(Class<Q> keyType) {
            Objects.requireNonNull(keyType);
            return new UniqueKeyIndex<>(consumer, uniqueType, primaryTypeKey,
                    keyType);
        }

        /**
         * Creates a {@link UniqueKeyIndex} for matching with a single key field path and type.
         *
         * @param keyFieldPath the key field path
         * @param keyFieldType the key type
         * @param <Q> the key type
         * @return a {@code UniqueKeyIndex}
         * @throws IllegalArgumentException if the key field path is empty or invalid
         * @throws IllegalArgumentException if the key field type is invalid given resolution of the
         * key field path
         * @throws IllegalArgumentException if the builder is bound to the primary key of the unique type and
         * the field path declared by the key type is not identical to the keyFieldPath
         */
        public <Q> UniqueKeyIndex<T, Q> usingPath(String keyFieldPath, Class<Q> keyFieldType) {
            Objects.requireNonNull(keyFieldPath);
            if (keyFieldPath.isEmpty()) {
                throw new IllegalArgumentException("keyFieldPath argument is an empty String");
            }
            Objects.requireNonNull(keyFieldType);
            return new UniqueKeyIndex<>(consumer, uniqueType, primaryTypeKey,
                    keyFieldPath, keyFieldType);
        }
    }
}

