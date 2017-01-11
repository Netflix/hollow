package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StoriesSynopsesDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getNarrativeTextOrdinal(int ordinal);

    public int getHooksOrdinal(int ordinal);

    public StoriesSynopsesTypeAPI getTypeAPI();

}