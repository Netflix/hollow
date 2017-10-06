package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StoriesSynopsesDataAccessor extends AbstractHollowDataAccessor<StoriesSynopsesHollow> {

    public static final String TYPE = "StoriesSynopsesHollow";
    private VMSHollowInputAPI api;

    public StoriesSynopsesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StoriesSynopsesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StoriesSynopsesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StoriesSynopsesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StoriesSynopsesHollow getRecord(int ordinal){
        return api.getStoriesSynopsesHollow(ordinal);
    }

}