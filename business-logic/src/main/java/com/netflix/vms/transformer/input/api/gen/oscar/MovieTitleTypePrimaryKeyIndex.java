package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieTitleType, K> uki = UniqueKeyIndex.from(consumer, MovieTitleType.class)
 *         .usingBean(k);
 *     MovieTitleType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieTitleType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieTitleTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieTitleType> implements HollowUniqueKeyIndex<MovieTitleType> {

    public MovieTitleTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieTitleTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieTitleType")).getPrimaryKey().getFieldPaths());
    }

    public MovieTitleTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieTitleTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieTitleType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieTitleType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieTitleType(ordinal);
    }

}