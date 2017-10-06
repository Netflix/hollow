package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamBoxInfoKeyDataAccessor extends AbstractHollowDataAccessor<StreamBoxInfoKeyHollow> {

    public static final String TYPE = "StreamBoxInfoKeyHollow";
    private VMSHollowInputAPI api;

    public StreamBoxInfoKeyDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamBoxInfoKeyDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamBoxInfoKeyDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamBoxInfoKeyDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamBoxInfoKeyHollow getRecord(int ordinal){
        return api.getStreamBoxInfoKeyHollow(ordinal);
    }

}