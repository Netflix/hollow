package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class VideoGeneralInteractiveDataDataAccessor extends AbstractHollowDataAccessor<VideoGeneralInteractiveData> {

    public static final String TYPE = "VideoGeneralInteractiveData";
    private VideoGeneralAPI api;

    public VideoGeneralInteractiveDataDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VideoGeneralAPI)consumer.getAPI();
    }

    public VideoGeneralInteractiveDataDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public VideoGeneralInteractiveDataDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public VideoGeneralInteractiveDataDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public VideoGeneralInteractiveData getRecord(int ordinal){
        return api.getVideoGeneralInteractiveData(ordinal);
    }

}