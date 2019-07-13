package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ShowSeasonEpisodeDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public long getDisplaySetId(int ordinal);

    public Long getDisplaySetIdBoxed(int ordinal);

    public int getCountryCodesOrdinal(int ordinal);

    public int getSeasonsOrdinal(int ordinal);

    public boolean getHideSeasonNumbers(int ordinal);

    public Boolean getHideSeasonNumbersBoxed(int ordinal);

    public boolean getEpisodicNewBadge(int ordinal);

    public Boolean getEpisodicNewBadgeBoxed(int ordinal);

    public String getMerchOrder(int ordinal);

    public boolean isMerchOrderEqual(int ordinal, String testValue);

    public int getMerchOrderOrdinal(int ordinal);

    public ShowSeasonEpisodeTypeAPI getTypeAPI();

}