package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class FestivalsDescriptionHollow extends HollowObject {

    public FestivalsDescriptionHollow(FestivalsDescriptionDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public FestivalsDescriptionMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getFestivalsDescriptionMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public FestivalsDescriptionTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FestivalsDescriptionDelegate delegate() {
        return (FestivalsDescriptionDelegate)delegate;
    }

}