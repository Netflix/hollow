package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class FestivalsCopyrightHollow extends HollowObject {

    public FestivalsCopyrightHollow(FestivalsCopyrightDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public FestivalsCopyrightMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFestivalsCopyrightMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public FestivalsCopyrightTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FestivalsCopyrightDelegate delegate() {
        return (FestivalsCopyrightDelegate)delegate;
    }

}