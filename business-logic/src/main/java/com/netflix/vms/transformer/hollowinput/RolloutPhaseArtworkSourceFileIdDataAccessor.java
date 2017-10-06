package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RolloutPhaseArtworkSourceFileIdDataAccessor extends AbstractHollowDataAccessor<RolloutPhaseArtworkSourceFileIdHollow> {

    public static final String TYPE = "RolloutPhaseArtworkSourceFileIdHollow";
    private VMSHollowInputAPI api;

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RolloutPhaseArtworkSourceFileIdHollow getRecord(int ordinal){
        return api.getRolloutPhaseArtworkSourceFileIdHollow(ordinal);
    }

}