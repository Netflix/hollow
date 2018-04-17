package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsWindowPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, RightsWindowHollow> {

    public RightsWindowPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public RightsWindowPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("RightsWindow")).getPrimaryKey().getFieldPaths());
    }

    public RightsWindowPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RightsWindowPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RightsWindow", isListenToDataRefresh, fieldPaths);
    }

    public RightsWindowHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRightsWindowHollow(ordinal);
    }

}