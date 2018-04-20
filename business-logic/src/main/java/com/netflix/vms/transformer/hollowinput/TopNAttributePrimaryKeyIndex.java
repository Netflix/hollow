package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TopNAttributePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, TopNAttributeHollow> {

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("TopNAttribute")).getPrimaryKey().getFieldPaths());
    }

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TopNAttribute", isListenToDataRefresh, fieldPaths);
    }

    public TopNAttributeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTopNAttributeHollow(ordinal);
    }

}