package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MoviesTransliteratedHollow extends HollowObject {

    public MoviesTransliteratedHollow(MoviesTransliteratedDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public MoviesTransliteratedMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesTransliteratedMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MoviesTransliteratedTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MoviesTransliteratedDelegate delegate() {
        return (MoviesTransliteratedDelegate)delegate;
    }

}