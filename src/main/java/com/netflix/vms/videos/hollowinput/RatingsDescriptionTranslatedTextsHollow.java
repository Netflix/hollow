package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RatingsDescriptionTranslatedTextsHollow extends HollowObject {

    public RatingsDescriptionTranslatedTextsHollow(RatingsDescriptionTranslatedTextsDelegate delegate, int ordinal) {
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

    public RatingsDescriptionTranslatedTextsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RatingsDescriptionTranslatedTextsDelegate delegate() {
        return (RatingsDescriptionTranslatedTextsDelegate)delegate;
    }

}