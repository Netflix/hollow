package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RolloutPhaseLocalizedMetadataDataAccessor extends AbstractHollowDataAccessor<RolloutPhaseLocalizedMetadataHollow> {

    public static final String TYPE = "RolloutPhaseLocalizedMetadataHollow";
    private VMSHollowInputAPI api;

    public RolloutPhaseLocalizedMetadataDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RolloutPhaseLocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RolloutPhaseLocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RolloutPhaseLocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RolloutPhaseLocalizedMetadataHollow getRecord(int ordinal){
        return api.getRolloutPhaseLocalizedMetadataHollow(ordinal);
    }

}