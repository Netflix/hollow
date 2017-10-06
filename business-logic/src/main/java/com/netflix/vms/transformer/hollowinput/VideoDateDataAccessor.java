package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VideoDateDataAccessor extends AbstractHollowDataAccessor<VideoDateHollow> {

    public static final String TYPE = "VideoDateHollow";
    private VMSHollowInputAPI api;

    public VideoDateDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VideoDateDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VideoDateDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VideoDateDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VideoDateHollow getRecord(int ordinal){
        return api.getVideoDateHollow(ordinal);
    }

}