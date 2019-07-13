package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class VideoGeneralTitleTypeDataAccessor extends AbstractHollowDataAccessor<VideoGeneralTitleType> {

    public static final String TYPE = "VideoGeneralTitleType";
    private VideoGeneralAPI api;

    public VideoGeneralTitleTypeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VideoGeneralAPI)consumer.getAPI();
    }

    public VideoGeneralTitleTypeDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public VideoGeneralTitleTypeDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public VideoGeneralTitleTypeDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public VideoGeneralTitleType getRecord(int ordinal){
        return api.getVideoGeneralTitleType(ordinal);
    }

}