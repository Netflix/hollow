package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoTypeDescriptorDataAccessor extends AbstractHollowDataAccessor<VideoTypeDescriptorHollow> {

    public static final String TYPE = "VideoTypeDescriptorHollow";
    private VMSHollowInputAPI api;

    public VideoTypeDescriptorDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoTypeDescriptorDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoTypeDescriptorDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoTypeDescriptorDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoTypeDescriptorHollow getRecord(int ordinal){
        return api.getVideoTypeDescriptorHollow(ordinal);
    }

}