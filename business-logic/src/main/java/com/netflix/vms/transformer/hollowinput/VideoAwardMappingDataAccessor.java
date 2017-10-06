package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoAwardMappingDataAccessor extends AbstractHollowDataAccessor<VideoAwardMappingHollow> {

    public static final String TYPE = "VideoAwardMappingHollow";
    private VMSHollowInputAPI api;

    public VideoAwardMappingDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoAwardMappingDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoAwardMappingDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoAwardMappingDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoAwardMappingHollow getRecord(int ordinal){
        return api.getVideoAwardMappingHollow(ordinal);
    }

}