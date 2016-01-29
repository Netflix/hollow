package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CategoriesShortNameHollow extends HollowObject {

    public CategoriesShortNameHollow(CategoriesShortNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public CategoriesShortNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCategoriesShortNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CategoriesShortNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CategoriesShortNameDelegate delegate() {
        return (CategoriesShortNameDelegate)delegate;
    }

}