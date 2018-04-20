package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowSeasonEpisodePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ShowSeasonEpisodeHollow> {

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ShowSeasonEpisode")).getPrimaryKey().getFieldPaths());
    }

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ShowSeasonEpisode", isListenToDataRefresh, fieldPaths);
    }

    public ShowSeasonEpisodeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getShowSeasonEpisodeHollow(ordinal);
    }

}