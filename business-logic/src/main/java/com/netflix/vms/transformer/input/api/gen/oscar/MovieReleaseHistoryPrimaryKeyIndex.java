package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieReleaseHistory, K> uki = UniqueKeyIndex.from(consumer, MovieReleaseHistory.class)
 *         .usingBean(k);
 *     MovieReleaseHistory m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieReleaseHistory} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieReleaseHistoryPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieReleaseHistory> implements HollowUniqueKeyIndex<MovieReleaseHistory> {

    public MovieReleaseHistoryPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieReleaseHistoryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieReleaseHistory")).getPrimaryKey().getFieldPaths());
    }

    public MovieReleaseHistoryPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieReleaseHistoryPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieReleaseHistory", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieReleaseHistory findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieReleaseHistory(ordinal);
    }

}