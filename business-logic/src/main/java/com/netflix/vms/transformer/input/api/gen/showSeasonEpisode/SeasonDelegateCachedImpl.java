package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class SeasonDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, SeasonDelegate {

    private final Integer sequenceNumber;
    private final Long movieId;
    private final int episodesOrdinal;
    private final Boolean hideEpisodeNumbers;
    private final Boolean episodicNewBadge;
    private final Integer episodeSkipping;
    private final Boolean filterUnavailableEpisodes;
    private final Boolean useLatestEpisodeAsDefault;
    private final String merchOrder;
    private final int merchOrderOrdinal;
    private SeasonTypeAPI typeAPI;

    public SeasonDelegateCachedImpl(SeasonTypeAPI typeAPI, int ordinal) {
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.episodesOrdinal = typeAPI.getEpisodesOrdinal(ordinal);
        this.hideEpisodeNumbers = typeAPI.getHideEpisodeNumbersBoxed(ordinal);
        this.episodicNewBadge = typeAPI.getEpisodicNewBadgeBoxed(ordinal);
        this.episodeSkipping = typeAPI.getEpisodeSkippingBoxed(ordinal);
        this.filterUnavailableEpisodes = typeAPI.getFilterUnavailableEpisodesBoxed(ordinal);
        this.useLatestEpisodeAsDefault = typeAPI.getUseLatestEpisodeAsDefaultBoxed(ordinal);
        this.merchOrderOrdinal = typeAPI.getMerchOrderOrdinal(ordinal);
        int merchOrderTempOrdinal = merchOrderOrdinal;
        this.merchOrder = merchOrderTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(merchOrderTempOrdinal);
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

    public int getEpisodesOrdinal(int ordinal) {
        return episodesOrdinal;
    }

    public boolean getHideEpisodeNumbers(int ordinal) {
        if(hideEpisodeNumbers == null)
            return false;
        return hideEpisodeNumbers.booleanValue();
    }

    public Boolean getHideEpisodeNumbersBoxed(int ordinal) {
        return hideEpisodeNumbers;
    }

    public boolean getEpisodicNewBadge(int ordinal) {
        if(episodicNewBadge == null)
            return false;
        return episodicNewBadge.booleanValue();
    }

    public Boolean getEpisodicNewBadgeBoxed(int ordinal) {
        return episodicNewBadge;
    }

    public int getEpisodeSkipping(int ordinal) {
        if(episodeSkipping == null)
            return Integer.MIN_VALUE;
        return episodeSkipping.intValue();
    }

    public Integer getEpisodeSkippingBoxed(int ordinal) {
        return episodeSkipping;
    }

    public boolean getFilterUnavailableEpisodes(int ordinal) {
        if(filterUnavailableEpisodes == null)
            return false;
        return filterUnavailableEpisodes.booleanValue();
    }

    public Boolean getFilterUnavailableEpisodesBoxed(int ordinal) {
        return filterUnavailableEpisodes;
    }

    public boolean getUseLatestEpisodeAsDefault(int ordinal) {
        if(useLatestEpisodeAsDefault == null)
            return false;
        return useLatestEpisodeAsDefault.booleanValue();
    }

    public Boolean getUseLatestEpisodeAsDefaultBoxed(int ordinal) {
        return useLatestEpisodeAsDefault;
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

    public SeasonTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (SeasonTypeAPI) typeAPI;
    }

}