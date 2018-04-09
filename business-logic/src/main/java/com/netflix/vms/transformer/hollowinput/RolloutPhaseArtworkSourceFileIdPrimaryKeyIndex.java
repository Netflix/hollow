package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseArtworkSourceFileIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, RolloutPhaseArtworkSourceFileIdHollow> {

    public RolloutPhaseArtworkSourceFileIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public RolloutPhaseArtworkSourceFileIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("RolloutPhaseArtworkSourceFileId")).getPrimaryKey().getFieldPaths());
    }

    public RolloutPhaseArtworkSourceFileIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutPhaseArtworkSourceFileIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutPhaseArtworkSourceFileId", isListenToDataRefresh, fieldPaths);
    }

    public RolloutPhaseArtworkSourceFileIdHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutPhaseArtworkSourceFileIdHollow(ordinal);
    }

}