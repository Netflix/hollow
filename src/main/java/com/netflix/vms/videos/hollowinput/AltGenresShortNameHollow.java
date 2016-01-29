package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class AltGenresShortNameHollow extends HollowObject {

    public AltGenresShortNameHollow(AltGenresShortNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public AltGenresShortNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAltGenresShortNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public AltGenresShortNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AltGenresShortNameDelegate delegate() {
        return (AltGenresShortNameDelegate)delegate;
    }

}