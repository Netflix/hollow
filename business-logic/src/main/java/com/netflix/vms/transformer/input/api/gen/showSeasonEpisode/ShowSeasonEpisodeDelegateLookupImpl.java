package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
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

    public boolean getHideSeasonNumbers(int ordinal) {
        return typeAPI.getHideSeasonNumbers(ordinal);
    }

    public Boolean getHideSeasonNumbersBoxed(int ordinal) {
        return typeAPI.getHideSeasonNumbersBoxed(ordinal);
    }

    public boolean getEpisodicNewBadge(int ordinal) {
        return typeAPI.getEpisodicNewBadge(ordinal);
    }

    public Boolean getEpisodicNewBadgeBoxed(int ordinal) {
        return typeAPI.getEpisodicNewBadgeBoxed(ordinal);
    }

    public String getMerchOrder(int ordinal) {
        ordinal = typeAPI.getMerchOrderOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isMerchOrderEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getMerchOrderOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getMerchOrderOrdinal(int ordinal) {
        return typeAPI.getMerchOrderOrdinal(ordinal);
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