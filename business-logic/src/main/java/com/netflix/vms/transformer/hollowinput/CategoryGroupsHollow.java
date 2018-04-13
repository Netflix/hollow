package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class CategoryGroupsHollow extends HollowObject {

    public CategoryGroupsHollow(CategoryGroupsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getCategoryGroupId() {
        return delegate().getCategoryGroupId(ordinal);
    }

    public Long _getCategoryGroupIdBoxed() {
        return delegate().getCategoryGroupIdBoxed(ordinal);
    }

    public TranslatedTextHollow _getCategoryGroupName() {
        int refOrdinal = delegate().getCategoryGroupNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CategoryGroupsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CategoryGroupsDelegate delegate() {
        return (CategoryGroupsDelegate)delegate;
    }

}