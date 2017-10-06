package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RightsDataAccessor extends AbstractHollowDataAccessor<RightsHollow> {

    public static final String TYPE = "RightsHollow";
    private VMSHollowInputAPI api;

    public RightsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RightsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RightsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RightsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RightsHollow getRecord(int ordinal){
        return api.getRightsHollow(ordinal);
    }

}