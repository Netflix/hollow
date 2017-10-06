package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoRatingDataAccessor extends AbstractHollowDataAccessor<VideoRatingHollow> {

    public static final String TYPE = "VideoRatingHollow";
    private VMSHollowInputAPI api;

    public VideoRatingDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoRatingDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoRatingDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoRatingDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoRatingHollow getRecord(int ordinal){
        return api.getVideoRatingHollow(ordinal);
    }

}