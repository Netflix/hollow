package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DashStreamHeaderDataPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, DashStreamHeaderDataHollow> {

    public DashStreamHeaderDataPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public DashStreamHeaderDataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("DashStreamHeaderData")).getPrimaryKey().getFieldPaths());
    }

    public DashStreamHeaderDataPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DashStreamHeaderDataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "DashStreamHeaderData", isListenToDataRefresh, fieldPaths);
    }

    public DashStreamHeaderDataHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDashStreamHeaderDataHollow(ordinal);
    }

}