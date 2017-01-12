package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface EpisodesDelegate extends HollowObjectDelegate {

    public int getEpisodeNameOrdinal(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public long getEpisodeId(int ordinal);

    public Long getEpisodeIdBoxed(int ordinal);

    public EpisodesTypeAPI getTypeAPI();

}