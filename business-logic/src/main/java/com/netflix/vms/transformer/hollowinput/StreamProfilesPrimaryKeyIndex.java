package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamProfilesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, StreamProfilesHollow> {

    public StreamProfilesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public StreamProfilesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("StreamProfiles")).getPrimaryKey().getFieldPaths());
    }

    public StreamProfilesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StreamProfilesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "StreamProfiles", isListenToDataRefresh, fieldPaths);
    }

    public StreamProfilesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStreamProfilesHollow(ordinal);
    }

}