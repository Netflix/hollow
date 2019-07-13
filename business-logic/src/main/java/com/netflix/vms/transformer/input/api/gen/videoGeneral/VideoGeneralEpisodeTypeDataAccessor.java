package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class VideoGeneralEpisodeTypeDataAccessor extends AbstractHollowDataAccessor<VideoGeneralEpisodeType> {

    public static final String TYPE = "VideoGeneralEpisodeType";
    private VideoGeneralAPI api;

    public VideoGeneralEpisodeTypeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VideoGeneralAPI)consumer.getAPI();
    }

    public VideoGeneralEpisodeTypeDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public VideoGeneralEpisodeTypeDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public VideoGeneralEpisodeTypeDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public VideoGeneralEpisodeType getRecord(int ordinal){
        return api.getVideoGeneralEpisodeType(ordinal);
    }

}