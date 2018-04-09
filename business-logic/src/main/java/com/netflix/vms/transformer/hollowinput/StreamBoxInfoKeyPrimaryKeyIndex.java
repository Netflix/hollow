package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamBoxInfoKeyPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, StreamBoxInfoKeyHollow> {

    public StreamBoxInfoKeyPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public StreamBoxInfoKeyPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("StreamBoxInfoKey")).getPrimaryKey().getFieldPaths());
    }

    public StreamBoxInfoKeyPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StreamBoxInfoKeyPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "StreamBoxInfoKey", isListenToDataRefresh, fieldPaths);
    }

    public StreamBoxInfoKeyHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStreamBoxInfoKeyHollow(ordinal);
    }

}