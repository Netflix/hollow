package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface SeasonDelegate extends HollowObjectDelegate {

    public int getSequenceNumber(int ordinal);

    public Integer getSequenceNumberBoxed(int ordinal);

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

    public String getMerchOrder(int ordinal);

    public boolean isMerchOrderEqual(int ordinal, String testValue);

    public int getMerchOrderOrdinal(int ordinal);

    public SeasonTypeAPI getTypeAPI();

}