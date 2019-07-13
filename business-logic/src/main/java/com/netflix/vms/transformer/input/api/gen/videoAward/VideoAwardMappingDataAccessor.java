package com.netflix.vms.transformer.input.api.gen.videoAward;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class VideoAwardMappingDataAccessor extends AbstractHollowDataAccessor<VideoAwardMapping> {

    public static final String TYPE = "VideoAwardMapping";
    private VideoAwardAPI api;

    public VideoAwardMappingDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VideoAwardAPI)consumer.getAPI();
    }

    public VideoAwardMappingDataAccessor(HollowReadStateEngine rStateEngine, VideoAwardAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public VideoAwardMappingDataAccessor(HollowReadStateEngine rStateEngine, VideoAwardAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public VideoAwardMappingDataAccessor(HollowReadStateEngine rStateEngine, VideoAwardAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public VideoAwardMapping getRecord(int ordinal){
        return api.getVideoAwardMapping(ordinal);
    }

}