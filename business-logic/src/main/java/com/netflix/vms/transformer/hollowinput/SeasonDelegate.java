package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface SeasonDelegate extends HollowObjectDelegate {

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getEpisodesOrdinal(int ordinal);

    public boolean getHideEpisodeNumbers(int ordinal);

    public Boolean getHideEpisodeNumbersBoxed(int ordinal);

    public boolean getEpisodicNewBadge(int ordinal);

    public Boolean getEpisodicNewBadgeBoxed(int ordinal);

    public int getEpisodeSkipping(int ordinal);

    public Integer getEpisodeSkippingBoxed(int ordinal);

    public boolean getFilterUnavailableEpisodes(int ordinal);

    public Boolean getFilterUnavailableEpisodesBoxed(int ordinal);

    public boolean getUseLatestEpisodeAsDefault(int ordinal);

    public Boolean getUseLatestEpisodeAsDefaultBoxed(int ordinal);

    public int getMerchOrderOrdinal(int ordinal);

    public SeasonTypeAPI getTypeAPI();

}