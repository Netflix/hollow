package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StatusPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, StatusHollow> {

    public StatusPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public StatusPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Status")).getPrimaryKey().getFieldPaths());
    }

    public StatusPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StatusPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "Status", isListenToDataRefreah, fieldPaths);
    }

    public StatusHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStatusHollow(ordinal);
    }

}