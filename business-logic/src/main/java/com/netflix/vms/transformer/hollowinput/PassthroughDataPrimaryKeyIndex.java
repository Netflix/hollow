package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PassthroughDataPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, PassthroughDataHollow> {

    public PassthroughDataPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public PassthroughDataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("PassthroughData")).getPrimaryKey().getFieldPaths());
    }

    public PassthroughDataPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PassthroughDataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PassthroughData", isListenToDataRefresh, fieldPaths);
    }

    public PassthroughDataHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPassthroughDataHollow(ordinal);
    }

}