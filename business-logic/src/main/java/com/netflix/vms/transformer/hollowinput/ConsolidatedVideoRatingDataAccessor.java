package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ConsolidatedVideoRatingDataAccessor extends AbstractHollowDataAccessor<ConsolidatedVideoRatingHollow> {

    public static final String TYPE = "ConsolidatedVideoRatingHollow";
    private VMSHollowInputAPI api;

    public ConsolidatedVideoRatingDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ConsolidatedVideoRatingDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ConsolidatedVideoRatingDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ConsolidatedVideoRatingDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ConsolidatedVideoRatingHollow getRecord(int ordinal){
        return api.getConsolidatedVideoRatingHollow(ordinal);
    }

}