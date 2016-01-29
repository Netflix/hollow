package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class AwardsDescriptionHollow extends HollowObject {

    public AwardsDescriptionHollow(AwardsDescriptionDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public AwardsDescriptionTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAwardsDescriptionTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public AwardsDescriptionTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AwardsDescriptionDelegate delegate() {
        return (AwardsDescriptionDelegate)delegate;
    }

}