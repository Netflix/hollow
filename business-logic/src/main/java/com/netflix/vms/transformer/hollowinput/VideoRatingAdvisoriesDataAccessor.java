package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoRatingAdvisoriesDataAccessor extends AbstractHollowDataAccessor<VideoRatingAdvisoriesHollow> {

    public static final String TYPE = "VideoRatingAdvisoriesHollow";
    private VMSHollowInputAPI api;

    public VideoRatingAdvisoriesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoRatingAdvisoriesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoRatingAdvisoriesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoRatingAdvisoriesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoRatingAdvisoriesHollow getRecord(int ordinal){
        return api.getVideoRatingAdvisoriesHollow(ordinal);
    }

}