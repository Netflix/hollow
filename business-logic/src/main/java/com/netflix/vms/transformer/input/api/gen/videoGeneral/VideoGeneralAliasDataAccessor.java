package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class VideoGeneralAliasDataAccessor extends AbstractHollowDataAccessor<VideoGeneralAlias> {

    public static final String TYPE = "VideoGeneralAlias";
    private VideoGeneralAPI api;

    public VideoGeneralAliasDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VideoGeneralAPI)consumer.getAPI();
    }

    public VideoGeneralAliasDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public VideoGeneralAliasDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public VideoGeneralAliasDataAccessor(HollowReadStateEngine rStateEngine, VideoGeneralAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public VideoGeneralAlias getRecord(int ordinal){
        return api.getVideoGeneralAlias(ordinal);
    }

}