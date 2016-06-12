package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ShowSeasonEpisodeDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public long getDisplaySetId(int ordinal);

    public Long getDisplaySetIdBoxed(int ordinal);

    public int getCountryCodesOrdinal(int ordinal);

    public int getSeasonsOrdinal(int ordinal);

    public ShowSeasonEpisodeTypeAPI getTypeAPI();

}