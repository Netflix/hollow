package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<Season, K> uki = UniqueKeyIndex.from(consumer, Season.class)
 *         .usingBean(k);
 *     Season m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code Season} object.
 */
@Deprecated
@SuppressWarnings("all")
public class SeasonPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<ShowSeasonEpisodeAPI, Season> implements HollowUniqueKeyIndex<Season> {

    public SeasonPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public SeasonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Season")).getPrimaryKey().getFieldPaths());
    }

    public SeasonPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public SeasonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Season", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public Season findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getSeason(ordinal);
    }

}