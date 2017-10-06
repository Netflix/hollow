package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RolloutPhaseArtworkDataAccessor extends AbstractHollowDataAccessor<RolloutPhaseArtworkHollow> {

    public static final String TYPE = "RolloutPhaseArtworkHollow";
    private VMSHollowInputAPI api;

    public RolloutPhaseArtworkDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RolloutPhaseArtworkDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RolloutPhaseArtworkDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RolloutPhaseArtworkDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RolloutPhaseArtworkHollow getRecord(int ordinal){
        return api.getRolloutPhaseArtworkHollow(ordinal);
    }

}