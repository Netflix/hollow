package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface Stories_SynopsesDelegate extends HollowObjectDelegate {

    public int getNarrativeTextOrdinal(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getHooksOrdinal(int ordinal);

    public Stories_SynopsesTypeAPI getTypeAPI();

}