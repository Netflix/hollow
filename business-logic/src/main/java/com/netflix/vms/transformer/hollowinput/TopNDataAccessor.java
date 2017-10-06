package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class TopNDataAccessor extends AbstractHollowDataAccessor<TopNHollow> {

    public static final String TYPE = "TopNHollow";
    private VMSHollowInputAPI api;

    public TopNDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public TopNDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public TopNDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public TopNDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public TopNHollow getRecord(int ordinal){
        return api.getTopNHollow(ordinal);
    }

}