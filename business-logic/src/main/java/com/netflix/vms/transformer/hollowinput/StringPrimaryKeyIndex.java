package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StringPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, StringHollow> {

    public StringPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public StringPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("String")).getPrimaryKey().getFieldPaths());
    }

    public StringPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StringPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "String", isListenToDataRefresh, fieldPaths);
    }

    public StringHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStringHollow(ordinal);
    }

}