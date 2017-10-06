package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoGeneralEpisodeTypeDataAccessor extends AbstractHollowDataAccessor<VideoGeneralEpisodeTypeHollow> {

    public static final String TYPE = "VideoGeneralEpisodeTypeHollow";
    private VMSHollowInputAPI api;

    public VideoGeneralEpisodeTypeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoGeneralEpisodeTypeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoGeneralEpisodeTypeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoGeneralEpisodeTypeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoGeneralEpisodeTypeHollow getRecord(int ordinal){
        return api.getVideoGeneralEpisodeTypeHollow(ordinal);
    }

}