package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class OverrideSchedulePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, OverrideScheduleHollow> {

    public OverrideSchedulePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public OverrideSchedulePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("OverrideSchedule")).getPrimaryKey().getFieldPaths());
    }

    public OverrideSchedulePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public OverrideSchedulePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "OverrideSchedule", isListenToDataRefreah, fieldPaths);
    }

    public OverrideScheduleHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getOverrideScheduleHollow(ordinal);
    }

}