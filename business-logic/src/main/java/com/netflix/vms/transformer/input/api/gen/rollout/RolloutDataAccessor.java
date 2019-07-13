package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class RolloutDataAccessor extends AbstractHollowDataAccessor<Rollout> {

    public static final String TYPE = "Rollout";
    private RolloutAPI api;

    public RolloutDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (RolloutAPI)consumer.getAPI();
    }

    public RolloutDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public RolloutDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public RolloutDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public Rollout getRecord(int ordinal){
        return api.getRollout(ordinal);
    }

}