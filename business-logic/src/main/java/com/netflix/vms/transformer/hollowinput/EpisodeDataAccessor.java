package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class EpisodeDataAccessor extends AbstractHollowDataAccessor<EpisodeHollow> {

    public static final String TYPE = "EpisodeHollow";
    private VMSHollowInputAPI api;

    public EpisodeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public EpisodeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public EpisodeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public EpisodeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public EpisodeHollow getRecord(int ordinal){
        return api.getEpisodeHollow(ordinal);
    }

}