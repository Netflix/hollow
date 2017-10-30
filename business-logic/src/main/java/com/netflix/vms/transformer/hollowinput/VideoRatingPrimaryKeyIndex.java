package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoRatingPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoRatingHollow> {

    public VideoRatingPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoRating")).getPrimaryKey().getFieldPaths());
    }

    public VideoRatingPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoRatingPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "VideoRating", isListenToDataRefreah, fieldPaths);
    }

    public VideoRatingHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoRatingHollow(ordinal);
    }

}