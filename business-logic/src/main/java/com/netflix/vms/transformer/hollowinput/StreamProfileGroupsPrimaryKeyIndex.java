package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamProfileGroupsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, StreamProfileGroupsHollow> {

    public StreamProfileGroupsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public StreamProfileGroupsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("StreamProfileGroups")).getPrimaryKey().getFieldPaths());
    }

    public StreamProfileGroupsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StreamProfileGroupsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "StreamProfileGroups", isListenToDataRefreah, fieldPaths);
    }

    public StreamProfileGroupsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStreamProfileGroupsHollow(ordinal);
    }

}