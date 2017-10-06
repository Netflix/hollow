package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class TopNAttributeDataAccessor extends AbstractHollowDataAccessor<TopNAttributeHollow> {

    public static final String TYPE = "TopNAttributeHollow";
    private VMSHollowInputAPI api;

    public TopNAttributeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public TopNAttributeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public TopNAttributeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public TopNAttributeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public TopNAttributeHollow getRecord(int ordinal){
        return api.getTopNAttributeHollow(ordinal);
    }

}