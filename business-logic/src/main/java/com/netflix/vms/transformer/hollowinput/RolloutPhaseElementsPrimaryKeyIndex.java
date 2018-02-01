package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseElementsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, RolloutPhaseElementsHollow> {

    public RolloutPhaseElementsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public RolloutPhaseElementsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("RolloutPhaseElements")).getPrimaryKey().getFieldPaths());
    }

    public RolloutPhaseElementsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutPhaseElementsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutPhaseElements", isListenToDataRefresh, fieldPaths);
    }

    public RolloutPhaseElementsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutPhaseElementsHollow(ordinal);
    }

}