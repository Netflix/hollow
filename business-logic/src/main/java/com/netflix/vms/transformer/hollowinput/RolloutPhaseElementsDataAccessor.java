package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RolloutPhaseElementsDataAccessor extends AbstractHollowDataAccessor<RolloutPhaseElementsHollow> {

    public static final String TYPE = "RolloutPhaseElementsHollow";
    private VMSHollowInputAPI api;

    public RolloutPhaseElementsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RolloutPhaseElementsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RolloutPhaseElementsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RolloutPhaseElementsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RolloutPhaseElementsHollow getRecord(int ordinal){
        return api.getRolloutPhaseElementsHollow(ordinal);
    }

}