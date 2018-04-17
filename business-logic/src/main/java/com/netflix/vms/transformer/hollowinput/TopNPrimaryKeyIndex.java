package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TopNPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, TopNHollow> {

    public TopNPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public TopNPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("TopN")).getPrimaryKey().getFieldPaths());
    }

    public TopNPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TopNPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TopN", isListenToDataRefresh, fieldPaths);
    }

    public TopNHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTopNHollow(ordinal);
    }

}