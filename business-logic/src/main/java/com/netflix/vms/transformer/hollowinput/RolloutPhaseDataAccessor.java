package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RolloutPhaseDataAccessor extends AbstractHollowDataAccessor<RolloutPhaseHollow> {

    public static final String TYPE = "RolloutPhaseHollow";
    private VMSHollowInputAPI api;

    public RolloutPhaseDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RolloutPhaseDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RolloutPhaseDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RolloutPhaseDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RolloutPhaseHollow getRecord(int ordinal){
        return api.getRolloutPhaseHollow(ordinal);
    }

}