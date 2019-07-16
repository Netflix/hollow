package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieType, K> uki = UniqueKeyIndex.from(consumer, MovieType.class)
 *         .usingBean(k);
 *     MovieType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieType> implements HollowUniqueKeyIndex<MovieType> {

    public MovieTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieType")).getPrimaryKey().getFieldPaths());
    }

    public MovieTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieType(ordinal);
    }

}