package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ReleaseDateDataAccessor extends AbstractHollowDataAccessor<ReleaseDateHollow> {

    public static final String TYPE = "ReleaseDateHollow";
    private VMSHollowInputAPI api;

    public ReleaseDateDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ReleaseDateDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ReleaseDateDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ReleaseDateDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ReleaseDateHollow getRecord(int ordinal){
        return api.getReleaseDateHollow(ordinal);
    }

}