package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class FestivalsFestivalNameHollow extends HollowObject {

    public FestivalsFestivalNameHollow(FestivalsFestivalNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public FestivalsFestivalNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFestivalsFestivalNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public FestivalsFestivalNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FestivalsFestivalNameDelegate delegate() {
        return (FestivalsFestivalNameDelegate)delegate;
    }

}