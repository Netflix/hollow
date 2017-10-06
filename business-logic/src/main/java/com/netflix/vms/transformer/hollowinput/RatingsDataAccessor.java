package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RatingsDataAccessor extends AbstractHollowDataAccessor<RatingsHollow> {

    public static final String TYPE = "RatingsHollow";
    private VMSHollowInputAPI api;

    public RatingsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RatingsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RatingsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RatingsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RatingsHollow getRecord(int ordinal){
        return api.getRatingsHollow(ordinal);
    }

}