package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class EpisodePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, EpisodeHollow> {

    public EpisodePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public EpisodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Episode")).getPrimaryKey().getFieldPaths());
    }

    public EpisodePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public EpisodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Episode", isListenToDataRefresh, fieldPaths);
    }

    public EpisodeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getEpisodeHollow(ordinal);
    }

}