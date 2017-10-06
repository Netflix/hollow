package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ConsolidatedCertificationSystemsDataAccessor extends AbstractHollowDataAccessor<ConsolidatedCertificationSystemsHollow> {

    public static final String TYPE = "ConsolidatedCertificationSystemsHollow";
    private VMSHollowInputAPI api;

    public ConsolidatedCertificationSystemsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ConsolidatedCertificationSystemsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ConsolidatedCertificationSystemsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ConsolidatedCertificationSystemsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ConsolidatedCertificationSystemsHollow getRecord(int ordinal){
        return api.getConsolidatedCertificationSystemsHollow(ordinal);
    }

}