package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CategoryGroupsCategoryGroupNameTranslatedTextsHollow extends HollowObject {

    public CategoryGroupsCategoryGroupNameTranslatedTextsHollow(CategoryGroupsCategoryGroupNameTranslatedTextsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CategoryGroupsCategoryGroupNameTranslatedTextsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CategoryGroupsCategoryGroupNameTranslatedTextsDelegate delegate() {
        return (CategoryGroupsCategoryGroupNameTranslatedTextsDelegate)delegate;
    }

}