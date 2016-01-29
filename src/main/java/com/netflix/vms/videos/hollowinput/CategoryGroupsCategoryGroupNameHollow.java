package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CategoryGroupsCategoryGroupNameHollow extends HollowObject {

    public CategoryGroupsCategoryGroupNameHollow(CategoryGroupsCategoryGroupNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public CategoryGroupsCategoryGroupNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCategoryGroupsCategoryGroupNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CategoryGroupsCategoryGroupNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CategoryGroupsCategoryGroupNameDelegate delegate() {
        return (CategoryGroupsCategoryGroupNameDelegate)delegate;
    }

}