package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoRatingRatingReasonIdsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoRatingRatingReasonIdsHollow> {

    public VideoRatingRatingReasonIdsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public VideoRatingRatingReasonIdsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoRatingRatingReasonIds")).getPrimaryKey().getFieldPaths());
    }

    public VideoRatingRatingReasonIdsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoRatingRatingReasonIdsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoRatingRatingReasonIds", isListenToDataRefresh, fieldPaths);
    }

    public VideoRatingRatingReasonIdsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoRatingRatingReasonIdsHollow(ordinal);
    }

}