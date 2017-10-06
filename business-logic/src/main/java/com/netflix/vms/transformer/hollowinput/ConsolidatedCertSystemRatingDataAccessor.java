package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ConsolidatedCertSystemRatingDataAccessor extends AbstractHollowDataAccessor<ConsolidatedCertSystemRatingHollow> {

    public static final String TYPE = "ConsolidatedCertSystemRatingHollow";
    private VMSHollowInputAPI api;

    public ConsolidatedCertSystemRatingDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ConsolidatedCertSystemRatingDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ConsolidatedCertSystemRatingDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ConsolidatedCertSystemRatingDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ConsolidatedCertSystemRatingHollow getRecord(int ordinal){
        return api.getConsolidatedCertSystemRatingHollow(ordinal);
    }

}