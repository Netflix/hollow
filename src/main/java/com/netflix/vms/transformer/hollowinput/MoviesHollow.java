package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MoviesHollow extends HollowObject {

    public MoviesHollow(MoviesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public TranslatedTextHollow _getShortDisplayName() {
        int refOrdinal = delegate().getShortDisplayNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getSiteSynopsis() {
        int refOrdinal = delegate().getSiteSynopsisOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getOriginalTitle() {
        int refOrdinal = delegate().getOriginalTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getDisplayName() {
        int refOrdinal = delegate().getDisplayNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getAka() {
        int refOrdinal = delegate().getAkaOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getTransliterated() {
        int refOrdinal = delegate().getTransliteratedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getTvSynopsis() {
        int refOrdinal = delegate().getTvSynopsisOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
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