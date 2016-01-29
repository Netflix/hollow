package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CategoriesDelegate extends HollowObjectDelegate {

    public int getDisplayNameOrdinal(int ordinal);

    public int getShortNameOrdinal(int ordinal);

    public long getCategoryId(int ordinal);

    public Long getCategoryIdBoxed(int ordinal);

    public CategoriesTypeAPI getTypeAPI();

}