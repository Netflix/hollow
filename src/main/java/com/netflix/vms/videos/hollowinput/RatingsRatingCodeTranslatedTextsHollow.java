package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RatingsRatingCodeTranslatedTextsHollow extends HollowObject {

    public RatingsRatingCodeTranslatedTextsHollow(RatingsRatingCodeTranslatedTextsDelegate delegate, int ordinal) {
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

    public RatingsRatingCodeTranslatedTextsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RatingsRatingCodeTranslatedTextsDelegate delegate() {
        return (RatingsRatingCodeTranslatedTextsDelegate)delegate;
    }

}