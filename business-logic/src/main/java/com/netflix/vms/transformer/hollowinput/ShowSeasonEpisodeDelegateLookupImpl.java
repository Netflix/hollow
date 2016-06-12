package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ShowSeasonEpisodeDelegateLookupImpl extends HollowObjectAbstractDelegate implements ShowSeasonEpisodeDelegate {

    private final ShowSeasonEpisodeTypeAPI typeAPI;

    public ShowSeasonEpisodeDelegateLookupImpl(ShowSeasonEpisodeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public long getDisplaySetId(int ordinal) {
        return typeAPI.getDisplaySetId(ordinal);
    }

    public Long getDisplaySetIdBoxed(int ordinal) {
        return typeAPI.getDisplaySetIdBoxed(ordinal);
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return typeAPI.getCountryCodesOrdinal(ordinal);
    }

    public int getSeasonsOrdinal(int ordinal) {
        return typeAPI.getSeasonsOrdinal(ordinal);
    }

    public ShowSeasonEpisodeTypeAPI getTypeAPI() {
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