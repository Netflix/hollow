package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class FestivalsCopyrightTranslatedTextsHollow extends HollowObject {

    public FestivalsCopyrightTranslatedTextsHollow(FestivalsCopyrightTranslatedTextsDelegate delegate, int ordinal) {
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

    public FestivalsCopyrightTranslatedTextsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FestivalsCopyrightTranslatedTextsDelegate delegate() {
        return (FestivalsCopyrightTranslatedTextsDelegate)delegate;
    }

}