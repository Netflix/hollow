package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoRatingRatingReasonPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoRatingRatingReasonHollow> {

    public VideoRatingRatingReasonPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public VideoRatingRatingReasonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoRatingRatingReason")).getPrimaryKey().getFieldPaths());
    }

    public VideoRatingRatingReasonPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoRatingRatingReasonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoRatingRatingReason", isListenToDataRefresh, fieldPaths);
    }

    public VideoRatingRatingReasonHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoRatingRatingReasonHollow(ordinal);
    }

}