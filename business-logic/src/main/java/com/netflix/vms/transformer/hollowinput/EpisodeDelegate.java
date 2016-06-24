package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface EpisodeDelegate extends HollowObjectDelegate {

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public EpisodeTypeAPI getTypeAPI();

}