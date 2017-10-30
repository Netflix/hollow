package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class RolloutPhaseArtworkSourceFileIdDataAccessor extends AbstractHollowDataAccessor<RolloutPhaseArtworkSourceFileIdHollow> {

    public static final String TYPE = "RolloutPhaseArtworkSourceFileIdHollow";
    private VMSHollowInputAPI api;

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VMSHollowInputAPI)consumer.getAPI();
    }

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public RolloutPhaseArtworkSourceFileIdDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public RolloutPhaseArtworkSourceFileIdHollow getRecord(int ordinal){
        return api.getRolloutPhaseArtworkSourceFileIdHollow(ordinal);
    }

}