package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MoviesSiteSynopsisTranslatedTextsHollow extends HollowObject {

    public MoviesSiteSynopsisTranslatedTextsHollow(MoviesSiteSynopsisTranslatedTextsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MoviesSiteSynopsisTranslatedTextsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MoviesSiteSynopsisTranslatedTextsDelegate delegate() {
        return (MoviesSiteSynopsisTranslatedTextsDelegate)delegate;
    }

}