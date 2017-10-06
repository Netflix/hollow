package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ConsolidatedVideoCountryRatingDataAccessor extends AbstractHollowDataAccessor<ConsolidatedVideoCountryRatingHollow> {

    public static final String TYPE = "ConsolidatedVideoCountryRatingHollow";
    private VMSHollowInputAPI api;

    public ConsolidatedVideoCountryRatingDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ConsolidatedVideoCountryRatingDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ConsolidatedVideoCountryRatingDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ConsolidatedVideoCountryRatingDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ConsolidatedVideoCountryRatingHollow getRecord(int ordinal){
        return api.getConsolidatedVideoCountryRatingHollow(ordinal);
    }

}