package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoTypeDataAccessor extends AbstractHollowDataAccessor<VideoTypeHollow> {

    public static final String TYPE = "VideoTypeHollow";
    private VMSHollowInputAPI api;

    public VideoTypeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoTypeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoTypeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoTypeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoTypeHollow getRecord(int ordinal){
        return api.getVideoTypeHollow(ordinal);
    }

}