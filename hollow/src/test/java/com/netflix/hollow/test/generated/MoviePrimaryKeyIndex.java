package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<Movie, K> uki = UniqueKeyIndex.from(consumer, Movie.class)
 *         .usingBean(k);
 *     Movie m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code Movie} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MoviePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<AwardsAPI, Movie> implements HollowUniqueKeyIndex<Movie> {

    public MoviePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MoviePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Movie")).getPrimaryKey().getFieldPaths());
    }

    public MoviePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MoviePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Movie", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public Movie findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovie(ordinal);
    }

}