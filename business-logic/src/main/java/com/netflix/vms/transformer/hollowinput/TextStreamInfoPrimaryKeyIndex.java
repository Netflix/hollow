package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TextStreamInfoPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, TextStreamInfoHollow> {

    public TextStreamInfoPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public TextStreamInfoPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("TextStreamInfo")).getPrimaryKey().getFieldPaths());
    }

    public TextStreamInfoPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TextStreamInfoPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TextStreamInfo", isListenToDataRefresh, fieldPaths);
    }

    public TextStreamInfoHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTextStreamInfoHollow(ordinal);
    }

}