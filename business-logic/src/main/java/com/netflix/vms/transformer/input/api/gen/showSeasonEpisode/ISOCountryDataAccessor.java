package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class ISOCountryDataAccessor extends AbstractHollowDataAccessor<ISOCountry> {

    public static final String TYPE = "ISOCountry";
    private ShowSeasonEpisodeAPI api;

    public ISOCountryDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (ShowSeasonEpisodeAPI)consumer.getAPI();
    }

    public ISOCountryDataAccessor(HollowReadStateEngine rStateEngine, ShowSeasonEpisodeAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public ISOCountryDataAccessor(HollowReadStateEngine rStateEngine, ShowSeasonEpisodeAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public ISOCountryDataAccessor(HollowReadStateEngine rStateEngine, ShowSeasonEpisodeAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public ISOCountry getRecord(int ordinal){
        return api.getISOCountry(ordinal);
    }

}