package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MoviesAkaHollow extends HollowObject {

    public MoviesAkaHollow(MoviesAkaDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public MoviesAkaMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesAkaMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MoviesAkaTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MoviesAkaDelegate delegate() {
        return (MoviesAkaDelegate)delegate;
    }

}