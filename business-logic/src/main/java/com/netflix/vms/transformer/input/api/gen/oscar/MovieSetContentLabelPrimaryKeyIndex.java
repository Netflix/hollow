package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<MovieSetContentLabel, K> uki = UniqueKeyIndex.from(consumer, MovieSetContentLabel.class)
 *         .usingBean(k);
 *     MovieSetContentLabel m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code MovieSetContentLabel} object.
 */
@Deprecated
@SuppressWarnings("all")
public class MovieSetContentLabelPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, MovieSetContentLabel> implements HollowUniqueKeyIndex<MovieSetContentLabel> {

    public MovieSetContentLabelPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public MovieSetContentLabelPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("MovieSetContentLabel")).getPrimaryKey().getFieldPaths());
    }

    public MovieSetContentLabelPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MovieSetContentLabelPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "MovieSetContentLabel", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public MovieSetContentLabel findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMovieSetContentLabel(ordinal);
    }

}