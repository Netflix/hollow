package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MoviesDisplayNameHollow extends HollowObject {

    public MoviesDisplayNameHollow(MoviesDisplayNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public MoviesDisplayNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesDisplayNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MoviesDisplayNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MoviesDisplayNameDelegate delegate() {
        return (MoviesDisplayNameDelegate)delegate;
    }

}