package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieTitleNLS, K> uki = UniqueKeyIndex.from(consumer, MovieTitleNLS.class)
 *         .usingBean(k);
 *     MovieTitleNLS m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieTitleNLS} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieTitleNLSPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieTitleNLS> implements HollowUniqueKeyIndex<MovieTitleNLS> {

    public MovieTitleNLSPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieTitleNLSPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieTitleNLS")).getPrimaryKey().getFieldPaths());
    }

    public MovieTitleNLSPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieTitleNLSPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieTitleNLS", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieTitleNLS findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieTitleNLS(ordinal);
    }

}