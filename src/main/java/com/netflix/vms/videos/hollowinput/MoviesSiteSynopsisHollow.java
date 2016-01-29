package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MoviesSiteSynopsisHollow extends HollowObject {

    public MoviesSiteSynopsisHollow(MoviesSiteSynopsisDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public MoviesSiteSynopsisMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMoviesSiteSynopsisMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MoviesSiteSynopsisTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MoviesSiteSynopsisDelegate delegate() {
        return (MoviesSiteSynopsisDelegate)delegate;
    }

}