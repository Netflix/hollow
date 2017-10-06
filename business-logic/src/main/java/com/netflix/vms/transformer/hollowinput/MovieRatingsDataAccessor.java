package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class MovieRatingsDataAccessor extends AbstractHollowDataAccessor<MovieRatingsHollow> {

    public static final String TYPE = "MovieRatingsHollow";
    private VMSHollowInputAPI api;

    public MovieRatingsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public MovieRatingsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public MovieRatingsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public MovieRatingsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public MovieRatingsHollow getRecord(int ordinal){
        return api.getMovieRatingsHollow(ordinal);
    }

}