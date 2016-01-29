package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class AwardsAlternateNameHollow extends HollowObject {

    public AwardsAlternateNameHollow(AwardsAlternateNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public AwardsAlternateNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAwardsAlternateNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public AwardsAlternateNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AwardsAlternateNameDelegate delegate() {
        return (AwardsAlternateNameDelegate)delegate;
    }

}