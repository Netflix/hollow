package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StatusPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, StatusHollow> {

    public StatusPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public StatusPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Status")).getPrimaryKey().getFieldPaths());
    }

    public StatusPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StatusPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Status", isListenToDataRefresh, fieldPaths);
    }

    public StatusHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStatusHollow(ordinal);
    }

}