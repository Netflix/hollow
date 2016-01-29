package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MoviesOriginalTitleHollow extends HollowObject {

    public MoviesOriginalTitleHollow(MoviesOriginalTitleDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public MoviesOriginalTitleMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesOriginalTitleMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MoviesOriginalTitleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MoviesOriginalTitleDelegate delegate() {
        return (MoviesOriginalTitleDelegate)delegate;
    }

}