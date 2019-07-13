package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class EpisodeDelegateLookupImpl extends HollowObjectAbstractDelegate implements EpisodeDelegate {

    private final EpisodeTypeAPI typeAPI;

    public EpisodeDelegateLookupImpl(EpisodeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Integer getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public boolean getMidSeason(int ordinal) {
        return typeAPI.getMidSeason(ordinal);
    }

    public Boolean getMidSeasonBoxed(int ordinal) {
        return typeAPI.getMidSeasonBoxed(ordinal);
    }

    public boolean getSeasonFinale(int ordinal) {
        return typeAPI.getSeasonFinale(ordinal);
    }

    public Boolean getSeasonFinaleBoxed(int ordinal) {
        return typeAPI.getSeasonFinaleBoxed(ordinal);
    }

    public boolean getShowFinale(int ordinal) {
        return typeAPI.getShowFinale(ordinal);
    }

    public Boolean getShowFinaleBoxed(int ordinal) {
        return typeAPI.getShowFinaleBoxed(ordinal);
    }

    public EpisodeTypeAPI getTypeAPI() {
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