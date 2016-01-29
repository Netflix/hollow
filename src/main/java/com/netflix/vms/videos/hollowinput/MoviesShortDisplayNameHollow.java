package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MoviesShortDisplayNameHollow extends HollowObject {

    public MoviesShortDisplayNameHollow(MoviesShortDisplayNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public MoviesShortDisplayNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesShortDisplayNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MoviesShortDisplayNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MoviesShortDisplayNameDelegate delegate() {
        return (MoviesShortDisplayNameDelegate)delegate;
    }

}