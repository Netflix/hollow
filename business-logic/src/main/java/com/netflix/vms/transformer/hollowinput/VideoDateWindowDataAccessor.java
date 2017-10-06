package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoDateWindowDataAccessor extends AbstractHollowDataAccessor<VideoDateWindowHollow> {

    public static final String TYPE = "VideoDateWindowHollow";
    private VMSHollowInputAPI api;

    public VideoDateWindowDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoDateWindowDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoDateWindowDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoDateWindowDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoDateWindowHollow getRecord(int ordinal){
        return api.getVideoDateWindowHollow(ordinal);
    }

}