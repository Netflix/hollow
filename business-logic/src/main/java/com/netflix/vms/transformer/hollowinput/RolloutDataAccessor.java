package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RolloutDataAccessor extends AbstractHollowDataAccessor<RolloutHollow> {

    public static final String TYPE = "RolloutHollow";
    private VMSHollowInputAPI api;

    public RolloutDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RolloutDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RolloutDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RolloutDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RolloutHollow getRecord(int ordinal){
        return api.getRolloutHollow(ordinal);
    }

}