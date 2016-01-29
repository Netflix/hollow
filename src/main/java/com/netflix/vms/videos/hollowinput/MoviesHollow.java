package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MoviesHollow extends HollowObject {

    public MoviesHollow(MoviesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public MoviesShortDisplayNameHollow _getShortDisplayName() {
        int refOrdinal = delegate().getShortDisplayNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesShortDisplayNameHollow(refOrdinal);
    }

    public MoviesSiteSynopsisHollow _getSiteSynopsis() {
        int refOrdinal = delegate().getSiteSynopsisOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesSiteSynopsisHollow(refOrdinal);
    }

    public MoviesOriginalTitleHollow _getOriginalTitle() {
        int refOrdinal = delegate().getOriginalTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesOriginalTitleHollow(refOrdinal);
    }

    public MoviesDisplayNameHollow _getDisplayName() {
        int refOrdinal = delegate().getDisplayNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesDisplayNameHollow(refOrdinal);
    }

    public MoviesAkaHollow _getAka() {
        int refOrdinal = delegate().getAkaOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesAkaHollow(refOrdinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public MoviesTransliteratedHollow _getTransliterated() {
        int refOrdinal = delegate().getTransliteratedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesTransliteratedHollow(refOrdinal);
    }

    public MoviesTvSynopsisHollow _getTvSynopsis() {
        int refOrdinal = delegate().getTvSynopsisOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesTvSynopsisHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MoviesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MoviesDelegate delegate() {
        return (MoviesDelegate)delegate;
    }

}