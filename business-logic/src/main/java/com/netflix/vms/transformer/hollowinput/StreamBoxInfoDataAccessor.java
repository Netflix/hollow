package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamBoxInfoDataAccessor extends AbstractHollowDataAccessor<StreamBoxInfoHollow> {

    public static final String TYPE = "StreamBoxInfoHollow";
    private VMSHollowInputAPI api;

    public StreamBoxInfoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamBoxInfoDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamBoxInfoDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamBoxInfoDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamBoxInfoHollow getRecord(int ordinal){
        return api.getStreamBoxInfoHollow(ordinal);
    }

}