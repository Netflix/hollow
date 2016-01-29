package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class LanguagesNameHollow extends HollowObject {

    public LanguagesNameHollow(LanguagesNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public LanguagesNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getLanguagesNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public LanguagesNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected LanguagesNameDelegate delegate() {
        return (LanguagesNameDelegate)delegate;
    }

}