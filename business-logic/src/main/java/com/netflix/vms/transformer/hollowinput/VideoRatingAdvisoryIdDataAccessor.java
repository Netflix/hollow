package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoRatingAdvisoryIdDataAccessor extends AbstractHollowDataAccessor<VideoRatingAdvisoryIdHollow> {

    public static final String TYPE = "VideoRatingAdvisoryIdHollow";
    private VMSHollowInputAPI api;

    public VideoRatingAdvisoryIdDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoRatingAdvisoryIdDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoRatingAdvisoryIdDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoRatingAdvisoryIdDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoRatingAdvisoryIdHollow getRecord(int ordinal){
        return api.getVideoRatingAdvisoryIdHollow(ordinal);
    }

}