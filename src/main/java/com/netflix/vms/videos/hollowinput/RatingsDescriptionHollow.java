package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RatingsDescriptionHollow extends HollowObject {

    public RatingsDescriptionHollow(RatingsDescriptionDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public RatingsDescriptionMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRatingsDescriptionMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RatingsDescriptionTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RatingsDescriptionDelegate delegate() {
        return (RatingsDescriptionDelegate)delegate;
    }

}