package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class MovieDataAccessor extends AbstractHollowDataAccessor<Movie> {

    public static final String TYPE = "Movie";
    private AwardsAPI api;

    public MovieDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (AwardsAPI)consumer.getAPI();
    }

    public MovieDataAccessor(HollowReadStateEngine rStateEngine, AwardsAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public MovieDataAccessor(HollowReadStateEngine rStateEngine, AwardsAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public MovieDataAccessor(HollowReadStateEngine rStateEngine, AwardsAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public Movie getRecord(int ordinal){
        return api.getMovie(ordinal);
    }

}