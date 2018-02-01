package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowMemberTypesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ShowMemberTypesHollow> {

    public ShowMemberTypesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ShowMemberTypesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ShowMemberTypes")).getPrimaryKey().getFieldPaths());
    }

    public ShowMemberTypesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ShowMemberTypesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ShowMemberTypes", isListenToDataRefresh, fieldPaths);
    }

    public ShowMemberTypesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getShowMemberTypesHollow(ordinal);
    }

}