package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamDimensionsDataAccessor extends AbstractHollowDataAccessor<StreamDimensionsHollow> {

    public static final String TYPE = "StreamDimensionsHollow";
    private VMSHollowInputAPI api;

    public StreamDimensionsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamDimensionsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamDimensionsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamDimensionsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamDimensionsHollow getRecord(int ordinal){
        return api.getStreamDimensionsHollow(ordinal);
    }

}