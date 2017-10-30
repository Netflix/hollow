package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class ConsolidatedVideoCountryRatingDataAccessor extends AbstractHollowDataAccessor<ConsolidatedVideoCountryRatingHollow> {

    public static final String TYPE = "ConsolidatedVideoCountryRatingHollow";
    private VMSHollowInputAPI api;

    public ConsolidatedVideoCountryRatingDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VMSHollowInputAPI)consumer.getAPI();
    }

    public ConsolidatedVideoCountryRatingDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public ConsolidatedVideoCountryRatingDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public ConsolidatedVideoCountryRatingDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public ConsolidatedVideoCountryRatingHollow getRecord(int ordinal){
        return api.getConsolidatedVideoCountryRatingHollow(ordinal);
    }

}