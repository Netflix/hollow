package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RolloutPhaseWindowDataAccessor extends AbstractHollowDataAccessor<RolloutPhaseWindowHollow> {

    public static final String TYPE = "RolloutPhaseWindowHollow";
    private VMSHollowInputAPI api;

    public RolloutPhaseWindowDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RolloutPhaseWindowDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RolloutPhaseWindowDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RolloutPhaseWindowDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RolloutPhaseWindowHollow getRecord(int ordinal){
        return api.getRolloutPhaseWindowHollow(ordinal);
    }

}