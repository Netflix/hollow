package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface CategoriesDelegate extends HollowObjectDelegate {

    public long getCategoryId(int ordinal);

    public Long getCategoryIdBoxed(int ordinal);

    public int getDisplayNameOrdinal(int ordinal);

    public int getShortNameOrdinal(int ordinal);

    public CategoriesTypeAPI getTypeAPI();

}