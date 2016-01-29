package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class FestivalsShortNameHollow extends HollowObject {

    public FestivalsShortNameHollow(FestivalsShortNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public FestivalsShortNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFestivalsShortNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public FestivalsShortNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FestivalsShortNameDelegate delegate() {
        return (FestivalsShortNameDelegate)delegate;
    }

}