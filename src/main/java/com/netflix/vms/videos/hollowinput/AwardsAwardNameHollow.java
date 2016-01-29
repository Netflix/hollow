package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class AwardsAwardNameHollow extends HollowObject {

    public AwardsAwardNameHollow(AwardsAwardNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public AwardsAwardNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAwardsAwardNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public AwardsAwardNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AwardsAwardNameDelegate delegate() {
        return (AwardsAwardNameDelegate)delegate;
    }

}