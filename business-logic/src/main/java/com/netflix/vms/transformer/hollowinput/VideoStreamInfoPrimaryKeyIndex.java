package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoStreamInfoPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoStreamInfoHollow> {

    public VideoStreamInfoPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoStreamInfo")).getPrimaryKey().getFieldPaths());
    }

    public VideoStreamInfoPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoStreamInfoPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "VideoStreamInfo", isListenToDataRefreah, fieldPaths);
    }

    public VideoStreamInfoHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoStreamInfoHollow(ordinal);
    }

}