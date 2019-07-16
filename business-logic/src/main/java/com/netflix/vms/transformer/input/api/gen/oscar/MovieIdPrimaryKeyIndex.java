package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieId, K> uki = UniqueKeyIndex.from(consumer, MovieId.class)
 *         .usingBean(k);
 *     MovieId m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieId} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieId> implements HollowUniqueKeyIndex<MovieId> {

    public MovieIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieId")).getPrimaryKey().getFieldPaths());
    }

    public MovieIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieId", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieId findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieId(ordinal);
    }

}