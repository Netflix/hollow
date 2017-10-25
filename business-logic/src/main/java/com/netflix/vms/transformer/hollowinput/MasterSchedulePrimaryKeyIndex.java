package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MasterSchedulePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, MasterScheduleHollow> {

    public MasterSchedulePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("MasterSchedule")).getPrimaryKey().getFieldPaths());
    }

    public MasterSchedulePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public MasterSchedulePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "MasterSchedule", isListenToDataRefreah, fieldPaths);
    }

    public MasterScheduleHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getMasterScheduleHollow(ordinal);
    }

}