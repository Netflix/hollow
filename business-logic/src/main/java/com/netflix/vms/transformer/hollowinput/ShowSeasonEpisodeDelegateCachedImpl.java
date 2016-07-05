package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class ShowSeasonEpisodeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ShowSeasonEpisodeDelegate {

    private final Long movieId;
    private final Long displaySetId;
    private final int countryCodesOrdinal;
    private final int seasonsOrdinal;
   private ShowSeasonEpisodeTypeAPI typeAPI;

    public ShowSeasonEpisodeDelegateCachedImpl(ShowSeasonEpisodeTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.displaySetId = typeAPI.getDisplaySetIdBoxed(ordinal);
        this.countryCodesOrdinal = typeAPI.getCountryCodesOrdinal(ordinal);
        this.seasonsOrdinal = typeAPI.getSeasonsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public long getDisplaySetId(int ordinal) {
        return displaySetId.longValue();
    }

    public Long getDisplaySetIdBoxed(int ordinal) {
        return displaySetId;
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return countryCodesOrdinal;
    }

    public int getSeasonsOrdinal(int ordinal) {
        return seasonsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ShowSeasonEpisodeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ShowSeasonEpisodeTypeAPI) typeAPI;
    }

}