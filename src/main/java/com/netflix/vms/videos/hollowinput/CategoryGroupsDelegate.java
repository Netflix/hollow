package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CategoryGroupsDelegate extends HollowObjectDelegate {

    public int getCategoryGroupNameOrdinal(int ordinal);

    public long getCategoryGroupId(int ordinal);

    public Long getCategoryGroupIdBoxed(int ordinal);

    public CategoryGroupsTypeAPI getTypeAPI();

}