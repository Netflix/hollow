package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoRatingRatingDataAccessor extends AbstractHollowDataAccessor<VideoRatingRatingHollow> {

    public static final String TYPE = "VideoRatingRatingHollow";
    private VMSHollowInputAPI api;

    public VideoRatingRatingDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoRatingRatingDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoRatingRatingDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoRatingRatingDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoRatingRatingHollow getRecord(int ordinal){
        return api.getVideoRatingRatingHollow(ordinal);
    }

}