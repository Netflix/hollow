package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class RolloutPhaseLocalizedMetadataDataAccessor extends AbstractHollowDataAccessor<RolloutPhaseLocalizedMetadata> {

    public static final String TYPE = "RolloutPhaseLocalizedMetadata";
    private RolloutAPI api;

    public RolloutPhaseLocalizedMetadataDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (RolloutAPI)consumer.getAPI();
    }

    public RolloutPhaseLocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public RolloutPhaseLocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public RolloutPhaseLocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public RolloutPhaseLocalizedMetadata getRecord(int ordinal){
        return api.getRolloutPhaseLocalizedMetadata(ordinal);
    }

}