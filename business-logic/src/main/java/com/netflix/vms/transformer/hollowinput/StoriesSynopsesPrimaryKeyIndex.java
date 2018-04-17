package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StoriesSynopsesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, StoriesSynopsesHollow> {

    public StoriesSynopsesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public StoriesSynopsesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("StoriesSynopses")).getPrimaryKey().getFieldPaths());
    }

    public StoriesSynopsesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StoriesSynopsesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "StoriesSynopses", isListenToDataRefresh, fieldPaths);
    }

    public StoriesSynopsesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStoriesSynopsesHollow(ordinal);
    }

}