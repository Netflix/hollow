package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class EpisodesDataAccessor extends AbstractHollowDataAccessor<EpisodesHollow> {

    public static final String TYPE = "EpisodesHollow";
    private VMSHollowInputAPI api;

    public EpisodesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public EpisodesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public EpisodesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public EpisodesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public EpisodesHollow getRecord(int ordinal){
        return api.getEpisodesHollow(ordinal);
    }

}