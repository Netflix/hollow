package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoGeneralDataAccessor extends AbstractHollowDataAccessor<VideoGeneralHollow> {

    public static final String TYPE = "VideoGeneralHollow";
    private VMSHollowInputAPI api;

    public VideoGeneralDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoGeneralDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoGeneralDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoGeneralDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoGeneralHollow getRecord(int ordinal){
        return api.getVideoGeneralHollow(ordinal);
    }

}