package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PhaseTagPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, PhaseTagHollow> {

    public PhaseTagPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public PhaseTagPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("PhaseTag")).getPrimaryKey().getFieldPaths());
    }

    public PhaseTagPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PhaseTagPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PhaseTag", isListenToDataRefresh, fieldPaths);
    }

    public PhaseTagHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPhaseTagHollow(ordinal);
    }

}