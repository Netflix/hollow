package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class LongPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, LongHollow> {

    public LongPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public LongPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Long")).getPrimaryKey().getFieldPaths());
    }

    public LongPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public LongPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Long", isListenToDataRefresh, fieldPaths);
    }

    public LongHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getLongHollow(ordinal);
    }

}