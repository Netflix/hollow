package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowSeasonEpisodePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ShowSeasonEpisodeHollow> {

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ShowSeasonEpisode")).getPrimaryKey().getFieldPaths());
    }

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ShowSeasonEpisodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "ShowSeasonEpisode", isListenToDataRefreah, fieldPaths);
    }

    public ShowSeasonEpisodeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getShowSeasonEpisodeHollow(ordinal);
    }

}