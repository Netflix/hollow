package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CategoryGroupsDelegate extends HollowObjectDelegate {

    public long getCategoryGroupId(int ordinal);

    public Long getCategoryGroupIdBoxed(int ordinal);

    public int getCategoryGroupNameOrdinal(int ordinal);

    public CategoryGroupsTypeAPI getTypeAPI();

}