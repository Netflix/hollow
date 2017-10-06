package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamNonImageInfoDataAccessor extends AbstractHollowDataAccessor<StreamNonImageInfoHollow> {

    public static final String TYPE = "StreamNonImageInfoHollow";
    private VMSHollowInputAPI api;

    public StreamNonImageInfoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamNonImageInfoDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamNonImageInfoDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamNonImageInfoDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamNonImageInfoHollow getRecord(int ordinal){
        return api.getStreamNonImageInfoHollow(ordinal);
    }

}