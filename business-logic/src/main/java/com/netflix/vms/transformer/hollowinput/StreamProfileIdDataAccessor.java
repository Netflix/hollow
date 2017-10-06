package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamProfileIdDataAccessor extends AbstractHollowDataAccessor<StreamProfileIdHollow> {

    public static final String TYPE = "StreamProfileIdHollow";
    private VMSHollowInputAPI api;

    public StreamProfileIdDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamProfileIdDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamProfileIdDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamProfileIdDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamProfileIdHollow getRecord(int ordinal){
        return api.getStreamProfileIdHollow(ordinal);
    }

}