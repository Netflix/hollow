package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoRatingRatingReasonIdsDataAccessor extends AbstractHollowDataAccessor<VideoRatingRatingReasonIdsHollow> {

    public static final String TYPE = "VideoRatingRatingReasonIdsHollow";
    private VMSHollowInputAPI api;

    public VideoRatingRatingReasonIdsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoRatingRatingReasonIdsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoRatingRatingReasonIdsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoRatingRatingReasonIdsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoRatingRatingReasonIdsHollow getRecord(int ordinal){
        return api.getVideoRatingRatingReasonIdsHollow(ordinal);
    }

}