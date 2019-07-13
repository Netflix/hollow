package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<ShowSeasonEpisode, K> uki = UniqueKeyIndex.from(consumer, ShowSeasonEpisode.class)
 *         .usingBean(k);
 *     ShowSeasonEpisode m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code ShowSeasonEpisode} object.
 */
@Deprecated
@SuppressWarnings("all")
public class ShowSeasonEpisodePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<ShowSeasonEpisodeAPI, ShowSeasonEpisode> implements HollowUniqueKeyIndex<ShowSeasonEpisode> {

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("ShowSeasonEpisode")).getPrimaryKey().getFieldPaths());
    }

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ShowSeasonEpisode", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public ShowSeasonEpisode findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getShowSeasonEpisode(ordinal);
    }

}