package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TopNAttributePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, TopNAttributeHollow> {

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("TopNAttribute")).getPrimaryKey().getFieldPaths());
    }

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "TopNAttribute", isListenToDataRefreah, fieldPaths);
    }

    public TopNAttributeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTopNAttributeHollow(ordinal);
    }

}