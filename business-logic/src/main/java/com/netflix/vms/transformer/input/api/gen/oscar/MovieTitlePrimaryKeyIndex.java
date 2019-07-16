package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieTitle, K> uki = UniqueKeyIndex.from(consumer, MovieTitle.class)
 *         .usingBean(k);
 *     MovieTitle m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieTitle} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieTitlePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieTitle> implements HollowUniqueKeyIndex<MovieTitle> {

    public MovieTitlePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieTitlePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieTitle")).getPrimaryKey().getFieldPaths());
    }

    public MovieTitlePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieTitlePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieTitle", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieTitle findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieTitle(ordinal);
    }

}