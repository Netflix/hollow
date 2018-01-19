package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoRatingRatingReasonPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoRatingRatingReasonHollow> {

    public VideoRatingRatingReasonPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public VideoRatingRatingReasonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoRatingRatingReason")).getPrimaryKey().getFieldPaths());
    }

    public VideoRatingRatingReasonPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoRatingRatingReasonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "VideoRatingRatingReason", isListenToDataRefreah, fieldPaths);
    }

    public VideoRatingRatingReasonHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoRatingRatingReasonHollow(ordinal);
    }

}