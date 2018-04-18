package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class SeasonHollow extends HollowObject {

    public SeasonHollow(SeasonDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public EpisodeListHollow _getEpisodes() {
        int refOrdinal = delegate().getEpisodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getEpisodeListHollow(refOrdinal);
    }

    public boolean _getHideEpisodeNumbers() {
        return delegate().getHideEpisodeNumbers(ordinal);
    }

    public Boolean _getHideEpisodeNumbersBoxed() {
        return delegate().getHideEpisodeNumbersBoxed(ordinal);
    }

    public boolean _getEpisodicNewBadge() {
        return delegate().getEpisodicNewBadge(ordinal);
    }

    public Boolean _getEpisodicNewBadgeBoxed() {
        return delegate().getEpisodicNewBadgeBoxed(ordinal);
    }

    public int _getEpisodeSkipping() {
        return delegate().getEpisodeSkipping(ordinal);
    }

    public Integer _getEpisodeSkippingBoxed() {
        return delegate().getEpisodeSkippingBoxed(ordinal);
    }

    public boolean _getFilterUnavailableEpisodes() {
        return delegate().getFilterUnavailableEpisodes(ordinal);
    }

    public Boolean _getFilterUnavailableEpisodesBoxed() {
        return delegate().getFilterUnavailableEpisodesBoxed(ordinal);
    }

    public boolean _getUseLatestEpisodeAsDefault() {
        return delegate().getUseLatestEpisodeAsDefault(ordinal);
    }

    public Boolean _getUseLatestEpisodeAsDefaultBoxed() {
        return delegate().getUseLatestEpisodeAsDefaultBoxed(ordinal);
    }

    public StringHollow _getMerchOrder() {
        int refOrdinal = delegate().getMerchOrderOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public SeasonTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected SeasonDelegate delegate() {
        return (SeasonDelegate)delegate;
    }

}