package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StoriesSynopsesHookDataAccessor extends AbstractHollowDataAccessor<StoriesSynopsesHookHollow> {

    public static final String TYPE = "StoriesSynopsesHookHollow";
    private VMSHollowInputAPI api;

    public StoriesSynopsesHookDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StoriesSynopsesHookDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StoriesSynopsesHookDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StoriesSynopsesHookDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StoriesSynopsesHookHollow getRecord(int ordinal){
        return api.getStoriesSynopsesHookHollow(ordinal);
    }

}