package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PhaseTagPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, PhaseTagHollow> {

    public PhaseTagPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public PhaseTagPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("PhaseTag")).getPrimaryKey().getFieldPaths());
    }

    public PhaseTagPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PhaseTagPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "PhaseTag", isListenToDataRefreah, fieldPaths);
    }

    public PhaseTagHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPhaseTagHollow(ordinal);
    }

}