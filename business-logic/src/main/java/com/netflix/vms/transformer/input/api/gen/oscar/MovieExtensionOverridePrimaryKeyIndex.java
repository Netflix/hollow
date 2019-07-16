package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieExtensionOverride, K> uki = UniqueKeyIndex.from(consumer, MovieExtensionOverride.class)
 *         .usingBean(k);
 *     MovieExtensionOverride m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieExtensionOverride} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieExtensionOverridePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieExtensionOverride> implements HollowUniqueKeyIndex<MovieExtensionOverride> {

    public MovieExtensionOverridePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieExtensionOverridePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieExtensionOverride")).getPrimaryKey().getFieldPaths());
    }

    public MovieExtensionOverridePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieExtensionOverridePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieExtensionOverride", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieExtensionOverride findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieExtensionOverride(ordinal);
    }

}