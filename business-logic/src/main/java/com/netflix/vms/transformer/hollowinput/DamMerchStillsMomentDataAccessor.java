package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DamMerchStillsMomentDataAccessor extends AbstractHollowDataAccessor<DamMerchStillsMomentHollow> {

    public static final String TYPE = "DamMerchStillsMomentHollow";
    private VMSHollowInputAPI api;

    public DamMerchStillsMomentDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DamMerchStillsMomentDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DamMerchStillsMomentDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DamMerchStillsMomentDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DamMerchStillsMomentHollow getRecord(int ordinal){
        return api.getDamMerchStillsMomentHollow(ordinal);
    }

}