package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhasePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, RolloutPhaseHollow> {

    public RolloutPhasePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public RolloutPhasePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("RolloutPhase")).getPrimaryKey().getFieldPaths());
    }

    public RolloutPhasePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutPhasePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutPhase", isListenToDataRefresh, fieldPaths);
    }

    public RolloutPhaseHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutPhaseHollow(ordinal);
    }

}