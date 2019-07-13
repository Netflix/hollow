package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowSeasonEpisodeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ShowSeasonEpisodeDelegate {

    private final Long movieId;
    private final Long displaySetId;
    private final int countryCodesOrdinal;
    private final int seasonsOrdinal;
    private final Boolean hideSeasonNumbers;
    private final Boolean episodicNewBadge;
    private final String merchOrder;
    private final int merchOrderOrdinal;
    private ShowSeasonEpisodeTypeAPI typeAPI;

    public ShowSeasonEpisodeDelegateCachedImpl(ShowSeasonEpisodeTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.displaySetId = typeAPI.getDisplaySetIdBoxed(ordinal);
        this.countryCodesOrdinal = typeAPI.getCountryCodesOrdinal(ordinal);
        this.seasonsOrdinal = typeAPI.getSeasonsOrdinal(ordinal);
        this.hideSeasonNumbers = typeAPI.getHideSeasonNumbersBoxed(ordinal);
        this.episodicNewBadge = typeAPI.getEpisodicNewBadgeBoxed(ordinal);
        this.merchOrderOrdinal = typeAPI.getMerchOrderOrdinal(ordinal);
        int merchOrderTempOrdinal = merchOrderOrdinal;
        this.merchOrder = merchOrderTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(merchOrderTempOrdinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public long getDisplaySetId(int ordinal) {
        if(displaySetId == null)
            return Long.MIN_VALUE;
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

    public boolean getHideSeasonNumbers(int ordinal) {
        if(hideSeasonNumbers == null)
            return false;
        return hideSeasonNumbers.booleanValue();
    }

    public Boolean getHideSeasonNumbersBoxed(int ordinal) {
        return hideSeasonNumbers;
    }

    public boolean getEpisodicNewBadge(int ordinal) {
        if(episodicNewBadge == null)
            return false;
        return episodicNewBadge.booleanValue();
    }

    public Boolean getEpisodicNewBadgeBoxed(int ordinal) {
        return episodicNewBadge;
    }

    public String getMerchOrder(int ordinal) {
        return merchOrder;
    }

    public boolean isMerchOrderEqual(int ordinal, String testValue) {
        if(testValue == null)
            return merchOrder == null;
        return testValue.equals(merchOrder);
    }

    public int getMerchOrderOrdinal(int ordinal) {
        return merchOrderOrdinal;
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