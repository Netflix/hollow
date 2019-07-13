package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class VideoTypeDataAccessor extends AbstractHollowDataAccessor<VideoType> {

    public static final String TYPE = "VideoType";
    private VideoTypeAPI api;

    public VideoTypeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VideoTypeAPI)consumer.getAPI();
    }

    public VideoTypeDataAccessor(HollowReadStateEngine rStateEngine, VideoTypeAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public VideoTypeDataAccessor(HollowReadStateEngine rStateEngine, VideoTypeAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public VideoTypeDataAccessor(HollowReadStateEngine rStateEngine, VideoTypeAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public VideoType getRecord(int ordinal){
        return api.getVideoType(ordinal);
    }

}