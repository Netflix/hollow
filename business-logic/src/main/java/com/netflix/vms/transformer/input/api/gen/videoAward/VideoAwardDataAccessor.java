package com.netflix.vms.transformer.input.api.gen.videoAward;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class VideoAwardDataAccessor extends AbstractHollowDataAccessor<VideoAward> {

    public static final String TYPE = "VideoAward";
    private VideoAwardAPI api;

    public VideoAwardDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VideoAwardAPI)consumer.getAPI();
    }

    public VideoAwardDataAccessor(HollowReadStateEngine rStateEngine, VideoAwardAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public VideoAwardDataAccessor(HollowReadStateEngine rStateEngine, VideoAwardAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public VideoAwardDataAccessor(HollowReadStateEngine rStateEngine, VideoAwardAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public VideoAward getRecord(int ordinal){
        return api.getVideoAward(ordinal);
    }

}