package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ProtectionTypesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ProtectionTypesHollow> {

    public ProtectionTypesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ProtectionTypesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ProtectionTypes")).getPrimaryKey().getFieldPaths());
    }

    public ProtectionTypesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ProtectionTypesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ProtectionTypes", isListenToDataRefresh, fieldPaths);
    }

    public ProtectionTypesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getProtectionTypesHollow(ordinal);
    }

}