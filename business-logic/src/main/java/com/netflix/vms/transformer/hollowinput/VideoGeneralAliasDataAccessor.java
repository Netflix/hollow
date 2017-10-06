package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoGeneralAliasDataAccessor extends AbstractHollowDataAccessor<VideoGeneralAliasHollow> {

    public static final String TYPE = "VideoGeneralAliasHollow";
    private VMSHollowInputAPI api;

    public VideoGeneralAliasDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoGeneralAliasDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoGeneralAliasDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoGeneralAliasDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoGeneralAliasHollow getRecord(int ordinal){
        return api.getVideoGeneralAliasHollow(ordinal);
    }

}