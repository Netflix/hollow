package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class Season extends HollowObject {

    public Season(SeasonDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Integer getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public EpisodeList getEpisodes() {
        int refOrdinal = delegate().getEpisodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getEpisodeList(refOrdinal);
    }

    public boolean getHideEpisodeNumbers() {
        return delegate().getHideEpisodeNumbers(ordinal);
    }

    public Boolean getHideEpisodeNumbersBoxed() {
        return delegate().getHideEpisodeNumbersBoxed(ordinal);
    }

    public boolean getEpisodicNewBadge() {
        return delegate().getEpisodicNewBadge(ordinal);
    }

    public Boolean getEpisodicNewBadgeBoxed() {
        return delegate().getEpisodicNewBadgeBoxed(ordinal);
    }

    public int getEpisodeSkipping() {
        return delegate().getEpisodeSkipping(ordinal);
    }

    public Integer getEpisodeSkippingBoxed() {
        return delegate().getEpisodeSkippingBoxed(ordinal);
    }

    public boolean getFilterUnavailableEpisodes() {
        return delegate().getFilterUnavailableEpisodes(ordinal);
    }

    public Boolean getFilterUnavailableEpisodesBoxed() {
        return delegate().getFilterUnavailableEpisodesBoxed(ordinal);
    }

    public boolean getUseLatestEpisodeAsDefault() {
        return delegate().getUseLatestEpisodeAsDefault(ordinal);
    }

    public Boolean getUseLatestEpisodeAsDefaultBoxed() {
        return delegate().getUseLatestEpisodeAsDefaultBoxed(ordinal);
    }

    public String getMerchOrder() {
        return delegate().getMerchOrder(ordinal);
    }

    public boolean isMerchOrderEqual(String testValue) {
        return delegate().isMerchOrderEqual(ordinal, testValue);
    }

    public HString getMerchOrderHollowReference() {
        int refOrdinal = delegate().getMerchOrderOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public ShowSeasonEpisodeAPI api() {
        return typeApi().getAPI();
    }

    public SeasonTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected SeasonDelegate delegate() {
        return (SeasonDelegate)delegate;
    }

}