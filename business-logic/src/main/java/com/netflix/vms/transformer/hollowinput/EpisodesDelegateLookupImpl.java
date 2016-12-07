package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class EpisodesDelegateLookupImpl extends HollowObjectAbstractDelegate implements EpisodesDelegate {

    private final EpisodesTypeAPI typeAPI;

    public EpisodesDelegateLookupImpl(EpisodesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getEpisodeNameOrdinal(int ordinal) {
        return typeAPI.getEpisodeNameOrdinal(ordinal);
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public long getEpisodeId(int ordinal) {
        return typeAPI.getEpisodeId(ordinal);
    }

    public Long getEpisodeIdBoxed(int ordinal) {
        return typeAPI.getEpisodeIdBoxed(ordinal);
    }

    public EpisodesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}