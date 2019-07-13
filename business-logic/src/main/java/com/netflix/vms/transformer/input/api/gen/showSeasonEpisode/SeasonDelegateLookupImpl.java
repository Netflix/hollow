package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class SeasonDelegateLookupImpl extends HollowObjectAbstractDelegate implements SeasonDelegate {

    private final SeasonTypeAPI typeAPI;

    public SeasonDelegateLookupImpl(SeasonTypeAPI typeAPI) {
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

    public int getEpisodesOrdinal(int ordinal) {
        return typeAPI.getEpisodesOrdinal(ordinal);
    }

    public boolean getHideEpisodeNumbers(int ordinal) {
        return typeAPI.getHideEpisodeNumbers(ordinal);
    }

    public Boolean getHideEpisodeNumbersBoxed(int ordinal) {
        return typeAPI.getHideEpisodeNumbersBoxed(ordinal);
    }

    public boolean getEpisodicNewBadge(int ordinal) {
        return typeAPI.getEpisodicNewBadge(ordinal);
    }

    public Boolean getEpisodicNewBadgeBoxed(int ordinal) {
        return typeAPI.getEpisodicNewBadgeBoxed(ordinal);
    }

    public int getEpisodeSkipping(int ordinal) {
        return typeAPI.getEpisodeSkipping(ordinal);
    }

    public Integer getEpisodeSkippingBoxed(int ordinal) {
        return typeAPI.getEpisodeSkippingBoxed(ordinal);
    }

    public boolean getFilterUnavailableEpisodes(int ordinal) {
        return typeAPI.getFilterUnavailableEpisodes(ordinal);
    }

    public Boolean getFilterUnavailableEpisodesBoxed(int ordinal) {
        return typeAPI.getFilterUnavailableEpisodesBoxed(ordinal);
    }

    public boolean getUseLatestEpisodeAsDefault(int ordinal) {
        return typeAPI.getUseLatestEpisodeAsDefault(ordinal);
    }

    public Boolean getUseLatestEpisodeAsDefaultBoxed(int ordinal) {
        return typeAPI.getUseLatestEpisodeAsDefaultBoxed(ordinal);
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

    public SeasonTypeAPI getTypeAPI() {
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