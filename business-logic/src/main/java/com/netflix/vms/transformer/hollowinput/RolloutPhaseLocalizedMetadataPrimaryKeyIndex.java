package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseLocalizedMetadataPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, RolloutPhaseLocalizedMetadataHollow> {

    public RolloutPhaseLocalizedMetadataPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public RolloutPhaseLocalizedMetadataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("RolloutPhaseLocalizedMetadata")).getPrimaryKey().getFieldPaths());
    }

    public RolloutPhaseLocalizedMetadataPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutPhaseLocalizedMetadataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutPhaseLocalizedMetadata", isListenToDataRefresh, fieldPaths);
    }

    public RolloutPhaseLocalizedMetadataHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutPhaseLocalizedMetadataHollow(ordinal);
    }

}