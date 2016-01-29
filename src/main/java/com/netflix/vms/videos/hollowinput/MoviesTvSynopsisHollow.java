package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MoviesTvSynopsisHollow extends HollowObject {

    public MoviesTvSynopsisHollow(MoviesTvSynopsisDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public MoviesTvSynopsisMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesTvSynopsisMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MoviesTvSynopsisTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MoviesTvSynopsisDelegate delegate() {
        return (MoviesTvSynopsisDelegate)delegate;
    }

}