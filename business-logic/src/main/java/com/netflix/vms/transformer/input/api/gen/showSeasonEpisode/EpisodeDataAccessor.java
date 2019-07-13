package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class EpisodeDataAccessor extends AbstractHollowDataAccessor<Episode> {

    public static final String TYPE = "Episode";
    private ShowSeasonEpisodeAPI api;

    public EpisodeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (ShowSeasonEpisodeAPI)consumer.getAPI();
    }

    public EpisodeDataAccessor(HollowReadStateEngine rStateEngine, ShowSeasonEpisodeAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public EpisodeDataAccessor(HollowReadStateEngine rStateEngine, ShowSeasonEpisodeAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public EpisodeDataAccessor(HollowReadStateEngine rStateEngine, ShowSeasonEpisodeAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public Episode getRecord(int ordinal){
        return api.getEpisode(ordinal);
    }

}