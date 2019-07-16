package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieReleaseType, K> uki = UniqueKeyIndex.from(consumer, MovieReleaseType.class)
 *         .usingBean(k);
 *     MovieReleaseType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieReleaseType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieReleaseTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieReleaseType> implements HollowUniqueKeyIndex<MovieReleaseType> {

    public MovieReleaseTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieReleaseTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieReleaseType")).getPrimaryKey().getFieldPaths());
    }

    public MovieReleaseTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieReleaseTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieReleaseType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieReleaseType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieReleaseType(ordinal);
    }

}