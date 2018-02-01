package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoRatingAdvisoriesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoRatingAdvisoriesHollow> {

    public VideoRatingAdvisoriesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public VideoRatingAdvisoriesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoRatingAdvisories")).getPrimaryKey().getFieldPaths());
    }

    public VideoRatingAdvisoriesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoRatingAdvisoriesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoRatingAdvisories", isListenToDataRefresh, fieldPaths);
    }

    public VideoRatingAdvisoriesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoRatingAdvisoriesHollow(ordinal);
    }

}