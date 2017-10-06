package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CdnDataAccessor extends AbstractHollowDataAccessor<CdnHollow> {

    public static final String TYPE = "CdnHollow";
    private VMSHollowInputAPI api;

    public CdnDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CdnDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CdnDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CdnDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CdnHollow getRecord(int ordinal){
        return api.getCdnHollow(ordinal);
    }

}