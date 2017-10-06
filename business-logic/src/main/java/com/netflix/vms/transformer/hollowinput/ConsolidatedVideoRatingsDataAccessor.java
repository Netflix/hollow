package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ConsolidatedVideoRatingsDataAccessor extends AbstractHollowDataAccessor<ConsolidatedVideoRatingsHollow> {

    public static final String TYPE = "ConsolidatedVideoRatingsHollow";
    private VMSHollowInputAPI api;

    public ConsolidatedVideoRatingsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ConsolidatedVideoRatingsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ConsolidatedVideoRatingsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ConsolidatedVideoRatingsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ConsolidatedVideoRatingsHollow getRecord(int ordinal){
        return api.getConsolidatedVideoRatingsHollow(ordinal);
    }

}