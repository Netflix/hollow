package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class AwardsDataAccessor extends AbstractHollowDataAccessor<AwardsHollow> {

    public static final String TYPE = "AwardsHollow";
    private VMSHollowInputAPI api;

    public AwardsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public AwardsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public AwardsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public AwardsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public AwardsHollow getRecord(int ordinal){
        return api.getAwardsHollow(ordinal);
    }

}