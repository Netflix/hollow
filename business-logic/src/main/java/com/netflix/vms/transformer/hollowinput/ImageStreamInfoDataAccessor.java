package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ImageStreamInfoDataAccessor extends AbstractHollowDataAccessor<ImageStreamInfoHollow> {

    public static final String TYPE = "ImageStreamInfoHollow";
    private VMSHollowInputAPI api;

    public ImageStreamInfoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ImageStreamInfoDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ImageStreamInfoDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ImageStreamInfoDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ImageStreamInfoHollow getRecord(int ordinal){
        return api.getImageStreamInfoHollow(ordinal);
    }

}