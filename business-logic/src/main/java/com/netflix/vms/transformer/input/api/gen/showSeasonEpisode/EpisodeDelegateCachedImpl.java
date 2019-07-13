package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class EpisodeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, EpisodeDelegate {

    private final Integer sequenceNumber;
    private final Long movieId;
    private final Boolean midSeason;
    private final Boolean seasonFinale;
    private final Boolean showFinale;
    private EpisodeTypeAPI typeAPI;

    public EpisodeDelegateCachedImpl(EpisodeTypeAPI typeAPI, int ordinal) {
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.midSeason = typeAPI.getMidSeasonBoxed(ordinal);
        this.seasonFinale = typeAPI.getSeasonFinaleBoxed(ordinal);
        this.showFinale = typeAPI.getShowFinaleBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getSequenceNumber(int ordinal) {
        if(sequenceNumber == null)
            return Integer.MIN_VALUE;
        return sequenceNumber.intValue();
    }

    public Integer getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public boolean getMidSeason(int ordinal) {
        if(midSeason == null)
            return false;
        return midSeason.booleanValue();
    }

    public Boolean getMidSeasonBoxed(int ordinal) {
        return midSeason;
    }

    public boolean getSeasonFinale(int ordinal) {
        if(seasonFinale == null)
            return false;
        return seasonFinale.booleanValue();
    }

    public Boolean getSeasonFinaleBoxed(int ordinal) {
        return seasonFinale;
    }

    public boolean getShowFinale(int ordinal) {
        if(showFinale == null)
            return false;
        return showFinale.booleanValue();
    }

    public Boolean getShowFinaleBoxed(int ordinal) {
        return showFinale;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public EpisodeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (EpisodeTypeAPI) typeAPI;
    }

}