package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class AltGenresDisplayNameHollow extends HollowObject {

    public AltGenresDisplayNameHollow(AltGenresDisplayNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public AltGenresDisplayNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAltGenresDisplayNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public AltGenresDisplayNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AltGenresDisplayNameDelegate delegate() {
        return (AltGenresDisplayNameDelegate)delegate;
    }

}