package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MoviesDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getShortDisplayNameOrdinal(int ordinal);

    public int getSiteSynopsisOrdinal(int ordinal);

    public int getOriginalTitleOrdinal(int ordinal);

    public int getDisplayNameOrdinal(int ordinal);

    public int getAkaOrdinal(int ordinal);

    public int getTransliteratedOrdinal(int ordinal);

    public int getTvSynopsisOrdinal(int ordinal);

    public MoviesTypeAPI getTypeAPI();

}