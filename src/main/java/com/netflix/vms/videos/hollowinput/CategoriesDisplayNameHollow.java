package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CategoriesDisplayNameHollow extends HollowObject {

    public CategoriesDisplayNameHollow(CategoriesDisplayNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public CategoriesDisplayNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCategoriesDisplayNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CategoriesDisplayNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CategoriesDisplayNameDelegate delegate() {
        return (CategoriesDisplayNameDelegate)delegate;
    }

}