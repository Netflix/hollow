package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowMemberTypesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ShowMemberTypesHollow> {

    public ShowMemberTypesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public ShowMemberTypesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ShowMemberTypes")).getPrimaryKey().getFieldPaths());
    }

    public ShowMemberTypesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ShowMemberTypesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "ShowMemberTypes", isListenToDataRefreah, fieldPaths);
    }

    public ShowMemberTypesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getShowMemberTypesHollow(ordinal);
    }

}