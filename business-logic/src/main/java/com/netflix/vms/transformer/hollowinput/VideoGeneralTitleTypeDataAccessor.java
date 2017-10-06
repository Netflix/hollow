package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoGeneralTitleTypeDataAccessor extends AbstractHollowDataAccessor<VideoGeneralTitleTypeHollow> {

    public static final String TYPE = "VideoGeneralTitleTypeHollow";
    private VMSHollowInputAPI api;

    public VideoGeneralTitleTypeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoGeneralTitleTypeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoGeneralTitleTypeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoGeneralTitleTypeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoGeneralTitleTypeHollow getRecord(int ordinal){
        return api.getVideoGeneralTitleTypeHollow(ordinal);
    }

}