package com.netflix.vms.transformer.input.api.gen.mclEarliestDate;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class FeedMovieCountryLanguagesDataAccessor extends AbstractHollowDataAccessor<FeedMovieCountryLanguages> {

    public static final String TYPE = "FeedMovieCountryLanguages";
    private MclEarliestDateAPI api;

    public FeedMovieCountryLanguagesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (MclEarliestDateAPI)consumer.getAPI();
    }

    public FeedMovieCountryLanguagesDataAccessor(HollowReadStateEngine rStateEngine, MclEarliestDateAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public FeedMovieCountryLanguagesDataAccessor(HollowReadStateEngine rStateEngine, MclEarliestDateAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public FeedMovieCountryLanguagesDataAccessor(HollowReadStateEngine rStateEngine, MclEarliestDateAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public FeedMovieCountryLanguages getRecord(int ordinal){
        return api.getFeedMovieCountryLanguages(ordinal);
    }

}