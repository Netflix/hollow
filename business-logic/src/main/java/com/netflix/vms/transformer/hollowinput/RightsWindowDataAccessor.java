package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RightsWindowDataAccessor extends AbstractHollowDataAccessor<RightsWindowHollow> {

    public static final String TYPE = "RightsWindowHollow";
    private VMSHollowInputAPI api;

    public RightsWindowDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RightsWindowDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RightsWindowDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RightsWindowDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RightsWindowHollow getRecord(int ordinal){
        return api.getRightsWindowHollow(ordinal);
    }

}