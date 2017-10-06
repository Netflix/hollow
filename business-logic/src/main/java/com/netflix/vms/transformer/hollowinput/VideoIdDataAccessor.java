package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoIdDataAccessor extends AbstractHollowDataAccessor<VideoIdHollow> {

    public static final String TYPE = "VideoIdHollow";
    private VMSHollowInputAPI api;

    public VideoIdDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoIdDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoIdDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoIdDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoIdHollow getRecord(int ordinal){
        return api.getVideoIdHollow(ordinal);
    }

}