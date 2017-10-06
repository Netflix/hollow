package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoArtworkSourceDataAccessor extends AbstractHollowDataAccessor<VideoArtworkSourceHollow> {

    public static final String TYPE = "VideoArtworkSourceHollow";
    private VMSHollowInputAPI api;

    public VideoArtworkSourceDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoArtworkSourceDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoArtworkSourceDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoArtworkSourceDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoArtworkSourceHollow getRecord(int ordinal){
        return api.getVideoArtworkSourceHollow(ordinal);
    }

}