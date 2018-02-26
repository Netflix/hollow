package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoRatingAdvisoryIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoRatingAdvisoryIdHollow> {

    public VideoRatingAdvisoryIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public VideoRatingAdvisoryIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoRatingAdvisoryId")).getPrimaryKey().getFieldPaths());
    }

    public VideoRatingAdvisoryIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoRatingAdvisoryIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "VideoRatingAdvisoryId", isListenToDataRefreah, fieldPaths);
    }

    public VideoRatingAdvisoryIdHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoRatingAdvisoryIdHollow(ordinal);
    }

}