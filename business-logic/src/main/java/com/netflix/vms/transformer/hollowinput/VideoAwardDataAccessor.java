package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoAwardDataAccessor extends AbstractHollowDataAccessor<VideoAwardHollow> {

    public static final String TYPE = "VideoAwardHollow";
    private VMSHollowInputAPI api;

    public VideoAwardDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoAwardDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoAwardDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoAwardDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoAwardHollow getRecord(int ordinal){
        return api.getVideoAwardHollow(ordinal);
    }

}