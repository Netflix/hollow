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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A type safe index for indexing with a unique key (such as a primary key).
 *
 * @param <T> the unique type
 * @param <Q> the key type
 */
public class UniqueKeyIndex<T extends HollowObject, Q> {
    final HollowConsumer consumer;
    HollowAPI api;
    boolean listenToDataRefresh;
    final SelectFieldPathResultExtractor<T> uniqueTypeExtractor;
    final List<MatchFieldPathArgumentExtractor<Q>> matchFields;
    final String uniqueTypeName;
    final String[] matchFieldPaths;
    HollowPrimaryKeyIndex hpki;
    RefreshListener refreshListener;

    UniqueKeyIndex(
            HollowConsumer consumer,
            Class<T> uniqueType,
            PrimaryKey primaryTypeKey,
            boolean listenToDataRefresh,
            List<MatchFieldPathArgumentExtractor<Q>> matchFields) {
        this.consumer = consumer;
        this.api = consumer.getAPI();
        this.uniqueTypeName = uniqueType.getSimpleName();

        this.uniqueTypeExtractor = SelectFieldPathResultExtractor
                .from(consumer.getAPI().getClass(), consumer.getStateEngine(), uniqueType, "", uniqueType);

        if (primaryTypeKey != null) {
            matchFields = validatePrimaryKeyFieldPaths(consumer, uniqueTypeName, primaryTypeKey, matchFields);
        }

        this.matchFields = matchFields;
        this.matchFieldPaths = matchFields.stream()
                .map(mf -> mf.fieldPath.toString())
                .toArray(String[]::new);

        this.hpki = new HollowPrimaryKeyIndex(consumer.getStateEngine(), uniqueTypeName, matchFieldPaths);

        if (listenToDataRefresh) {
            listenToDataRefresh();
        }
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
            boolean listenToDataRefresh,
            Class<Q> matchFieldsType) {
        // @@@ Use FieldPaths.createFieldPathForPrimaryKey
        this(consumer,
                uniqueType,
                primaryTypeKey,
                listenToDataRefresh,
                MatchFieldPathArgumentExtractor
                        .fromHolderClass(consumer.getStateEngine(), uniqueType, matchFieldsType,
                                FieldPaths::createFieldPathForPrimaryKey));
    }

    UniqueKeyIndex(
            HollowConsumer consumer,
            Class<T> uniqueType,
            PrimaryKey primaryTypeKey,
            boolean listenToDataRefresh,
            String fieldPath, Class<Q> matchFieldType) {
        // @@@ Use FieldPaths.createFieldPathForPrimaryKey
        this(consumer,
                uniqueType,
                primaryTypeKey,
                listenToDataRefresh,
                Collections.singletonList(
                        MatchFieldPathArgumentExtractor
                                .fromPathAndType(consumer.getStateEngine(), uniqueType, fieldPath, matchFieldType,
                                        FieldPaths::createFieldPathForPrimaryKey)));
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

        hpki.listenForDeltaUpdates();
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

        hpki.detachFromDeltaUpdates();
        consumer.removeRefreshListener(refreshListener);
    }

    final class RefreshListener implements HollowConsumer.RefreshListener {
        @Override
        public void snapshotUpdateOccurred(HollowAPI refreshAPI, HollowReadStateEngine stateEngine, long version) {
            hpki.detachFromDeltaUpdates();
            hpki = new HollowPrimaryKeyIndex(consumer.getStateEngine(), hpki.getPrimaryKey());
            hpki.listenForDeltaUpdates();
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
        boolean listenToDataRefresh;

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
            String primaryTypeName = uniqueType.getSimpleName();
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
         * Configures the unique key index to listen to {@code HollowConsumer} version updates.
         * On an update the index recalculates so updated data will be reflected in the results of a query
         * (performed after the update).
         *
         * @return this builder
         */
        public Builder<T> listenToDataRefresh() {
            listenToDataRefresh = true;
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
            return new UniqueKeyIndex<>(consumer, uniqueType, primaryTypeKey, listenToDataRefresh,
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
            return new UniqueKeyIndex<>(consumer, uniqueType, primaryTypeKey, listenToDataRefresh,
                    keyFieldPath, keyFieldType);
        }
    }
}

