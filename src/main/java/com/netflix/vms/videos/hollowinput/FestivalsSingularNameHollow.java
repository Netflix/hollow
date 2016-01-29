package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class FestivalsSingularNameHollow extends HollowObject {

    public FestivalsSingularNameHollow(FestivalsSingularNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public FestivalsSingularNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFestivalsSingularNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public FestivalsSingularNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FestivalsSingularNameDelegate delegate() {
        return (FestivalsSingularNameDelegate)delegate;
    }

}