package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoStreamInfoDataAccessor extends AbstractHollowDataAccessor<VideoStreamInfoHollow> {

    public static final String TYPE = "VideoStreamInfoHollow";
    private VMSHollowInputAPI api;

    public VideoStreamInfoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoStreamInfoDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoStreamInfoDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoStreamInfoDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoStreamInfoHollow getRecord(int ordinal){
        return api.getVideoStreamInfoHollow(ordinal);
    }

}