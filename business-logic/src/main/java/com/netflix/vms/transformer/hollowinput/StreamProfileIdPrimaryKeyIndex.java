package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamProfileIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, StreamProfileIdHollow> {

    public StreamProfileIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public StreamProfileIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("StreamProfileId")).getPrimaryKey().getFieldPaths());
    }

    public StreamProfileIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StreamProfileIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "StreamProfileId", isListenToDataRefresh, fieldPaths);
    }

    public StreamProfileIdHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStreamProfileIdHollow(ordinal);
    }

}