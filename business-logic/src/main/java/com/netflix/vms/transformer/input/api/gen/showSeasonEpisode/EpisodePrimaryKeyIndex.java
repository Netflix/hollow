package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<Episode, K> uki = UniqueKeyIndex.from(consumer, Episode.class)
 *         .usingBean(k);
 *     Episode m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code Episode} object.
 */
@Deprecated
@SuppressWarnings("all")
public class EpisodePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<ShowSeasonEpisodeAPI, Episode> implements HollowUniqueKeyIndex<Episode> {

    public EpisodePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public EpisodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Episode")).getPrimaryKey().getFieldPaths());
    }

    public EpisodePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public EpisodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Episode", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public Episode findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getEpisode(ordinal);
    }

}