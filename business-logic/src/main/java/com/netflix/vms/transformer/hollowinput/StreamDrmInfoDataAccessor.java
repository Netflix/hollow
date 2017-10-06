package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamDrmInfoDataAccessor extends AbstractHollowDataAccessor<StreamDrmInfoHollow> {

    public static final String TYPE = "StreamDrmInfoHollow";
    private VMSHollowInputAPI api;

    public StreamDrmInfoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamDrmInfoDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamDrmInfoDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamDrmInfoDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamDrmInfoHollow getRecord(int ordinal){
        return api.getStreamDrmInfoHollow(ordinal);
    }

}