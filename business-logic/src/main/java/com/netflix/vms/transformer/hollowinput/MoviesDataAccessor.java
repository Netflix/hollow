package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class MoviesDataAccessor extends AbstractHollowDataAccessor<MoviesHollow> {

    public static final String TYPE = "MoviesHollow";
    private VMSHollowInputAPI api;

    public MoviesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public MoviesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public MoviesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public MoviesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public MoviesHollow getRecord(int ordinal){
        return api.getMoviesHollow(ordinal);
    }

}