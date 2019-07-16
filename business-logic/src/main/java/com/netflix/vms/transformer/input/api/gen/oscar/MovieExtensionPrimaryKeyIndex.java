package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieExtension, K> uki = UniqueKeyIndex.from(consumer, MovieExtension.class)
 *         .usingBean(k);
 *     MovieExtension m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieExtension} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieExtensionPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieExtension> implements HollowUniqueKeyIndex<MovieExtension> {

    public MovieExtensionPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieExtensionPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieExtension")).getPrimaryKey().getFieldPaths());
    }

    public MovieExtensionPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieExtensionPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieExtension", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieExtension findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieExtension(ordinal);
    }

}