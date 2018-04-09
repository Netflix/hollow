package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StorageGroupsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, StorageGroupsHollow> {

    public StorageGroupsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public StorageGroupsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("StorageGroups")).getPrimaryKey().getFieldPaths());
    }

    public StorageGroupsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StorageGroupsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "StorageGroups", isListenToDataRefresh, fieldPaths);
    }

    public StorageGroupsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStorageGroupsHollow(ordinal);
    }

}