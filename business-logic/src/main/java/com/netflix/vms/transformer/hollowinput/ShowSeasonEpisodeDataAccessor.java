package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ShowSeasonEpisodeDataAccessor extends AbstractHollowDataAccessor<ShowSeasonEpisodeHollow> {

    public static final String TYPE = "ShowSeasonEpisodeHollow";
    private VMSHollowInputAPI api;

    public ShowSeasonEpisodeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ShowSeasonEpisodeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ShowSeasonEpisodeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ShowSeasonEpisodeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ShowSeasonEpisodeHollow getRecord(int ordinal){
        return api.getShowSeasonEpisodeHollow(ordinal);
    }

}