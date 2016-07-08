package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface LocaleTerritoryCodeDelegate extends HollowObjectDelegate {

    public int getValueOrdinal(int ordinal);

    public LocaleTerritoryCodeTypeAPI getTypeAPI();

}