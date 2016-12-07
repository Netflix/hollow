package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class EpisodesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, EpisodesDelegate {

    private final int episodeNameOrdinal;
    private final Long movieId;
    private final Long episodeId;
   private EpisodesTypeAPI typeAPI;

    public EpisodesDelegateCachedImpl(EpisodesTypeAPI typeAPI, int ordinal) {
        this.episodeNameOrdinal = typeAPI.getEpisodeNameOrdinal(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.episodeId = typeAPI.getEpisodeIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getEpisodeNameOrdinal(int ordinal) {
        return episodeNameOrdinal;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public long getEpisodeId(int ordinal) {
        return episodeId.longValue();
    }

    public Long getEpisodeIdBoxed(int ordinal) {
        return episodeId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public EpisodesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (EpisodesTypeAPI) typeAPI;
    }

}