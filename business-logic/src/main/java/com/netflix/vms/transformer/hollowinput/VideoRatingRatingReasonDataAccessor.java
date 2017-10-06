package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoRatingRatingReasonDataAccessor extends AbstractHollowDataAccessor<VideoRatingRatingReasonHollow> {

    public static final String TYPE = "VideoRatingRatingReasonHollow";
    private VMSHollowInputAPI api;

    public VideoRatingRatingReasonDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoRatingRatingReasonDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoRatingRatingReasonDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoRatingRatingReasonDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoRatingRatingReasonHollow getRecord(int ordinal){
        return api.getVideoRatingRatingReasonHollow(ordinal);
    }

}