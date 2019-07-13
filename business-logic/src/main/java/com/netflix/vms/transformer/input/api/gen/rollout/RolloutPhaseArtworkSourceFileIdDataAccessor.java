package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class RolloutPhaseArtworkSourceFileIdDataAccessor extends AbstractHollowDataAccessor<RolloutPhaseArtworkSourceFileId> {

    public static final String TYPE = "RolloutPhaseArtworkSourceFileId";
    private RolloutAPI api;

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (RolloutAPI)consumer.getAPI();
    }

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public RolloutPhaseArtworkSourceFileId getRecord(int ordinal){
        return api.getRolloutPhaseArtworkSourceFileId(ordinal);
    }

}