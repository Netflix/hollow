package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoStreamCropParamsDataAccessor extends AbstractHollowDataAccessor<VideoStreamCropParamsHollow> {

    public static final String TYPE = "VideoStreamCropParamsHollow";
    private VMSHollowInputAPI api;

    public VideoStreamCropParamsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoStreamCropParamsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoStreamCropParamsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoStreamCropParamsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoStreamCropParamsHollow getRecord(int ordinal){
        return api.getVideoStreamCropParamsHollow(ordinal);
    }

}