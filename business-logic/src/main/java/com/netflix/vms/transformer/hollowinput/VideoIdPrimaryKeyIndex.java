package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoIdHollow> {

    public VideoIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoId")).getPrimaryKey().getFieldPaths());
    }

    public VideoIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "VideoId", isListenToDataRefreah, fieldPaths);
    }

    public VideoIdHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoIdHollow(ordinal);
    }

}