package com.netflix.vms.transformer.input.api.gen.showSeasonEpisode;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface EpisodeDelegate extends HollowObjectDelegate {

    public int getSequenceNumber(int ordinal);

    public Integer getSequenceNumberBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public boolean getMidSeason(int ordinal);

    public Boolean getMidSeasonBoxed(int ordinal);

    public boolean getSeasonFinale(int ordinal);

    public Boolean getSeasonFinaleBoxed(int ordinal);

    public boolean getShowFinale(int ordinal);

    public Boolean getShowFinaleBoxed(int ordinal);

    public EpisodeTypeAPI getTypeAPI();

}