package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class MovieCharacterPersonDataAccessor extends AbstractHollowDataAccessor<MovieCharacterPersonHollow> {

    public static final String TYPE = "MovieCharacterPersonHollow";
    private VMSHollowInputAPI api;

    public MovieCharacterPersonDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public MovieCharacterPersonDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public MovieCharacterPersonDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public MovieCharacterPersonDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public MovieCharacterPersonHollow getRecord(int ordinal){
        return api.getMovieCharacterPersonHollow(ordinal);
    }

}