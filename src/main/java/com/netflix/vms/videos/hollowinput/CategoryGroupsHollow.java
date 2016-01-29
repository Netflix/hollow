package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CategoryGroupsHollow extends HollowObject {

    public CategoryGroupsHollow(CategoryGroupsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public CategoryGroupsCategoryGroupNameHollow _getCategoryGroupName() {
        int refOrdinal = delegate().getCategoryGroupNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCategoryGroupsCategoryGroupNameHollow(refOrdinal);
    }

    public long _getCategoryGroupId() {
        return delegate().getCategoryGroupId(ordinal);
    }

    public Long _getCategoryGroupIdBoxed() {
        return delegate().getCategoryGroupIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CategoryGroupsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CategoryGroupsDelegate delegate() {
        return (CategoryGroupsDelegate)delegate;
    }

}