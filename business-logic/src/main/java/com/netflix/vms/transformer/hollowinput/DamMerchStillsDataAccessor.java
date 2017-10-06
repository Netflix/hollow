package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DamMerchStillsDataAccessor extends AbstractHollowDataAccessor<DamMerchStillsHollow> {

    public static final String TYPE = "DamMerchStillsHollow";
    private VMSHollowInputAPI api;

    public DamMerchStillsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DamMerchStillsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DamMerchStillsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DamMerchStillsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DamMerchStillsHollow getRecord(int ordinal){
        return api.getDamMerchStillsHollow(ordinal);
    }

}