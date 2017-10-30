package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class ConsolidatedCertSystemRatingDataAccessor extends AbstractHollowDataAccessor<ConsolidatedCertSystemRatingHollow> {

    public static final String TYPE = "ConsolidatedCertSystemRatingHollow";
    private VMSHollowInputAPI api;

    public ConsolidatedCertSystemRatingDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VMSHollowInputAPI)consumer.getAPI();
    }

    public ConsolidatedCertSystemRatingDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public ConsolidatedCertSystemRatingDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public ConsolidatedCertSystemRatingDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public ConsolidatedCertSystemRatingHollow getRecord(int ordinal){
        return api.getConsolidatedCertSystemRatingHollow(ordinal);
    }

}