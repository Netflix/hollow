package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PassthroughDataDataAccessor extends AbstractHollowDataAccessor<PassthroughDataHollow> {

    public static final String TYPE = "PassthroughDataHollow";
    private VMSHollowInputAPI api;

    public PassthroughDataDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PassthroughDataDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PassthroughDataDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PassthroughDataDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PassthroughDataHollow getRecord(int ordinal){
        return api.getPassthroughDataHollow(ordinal);
    }

}