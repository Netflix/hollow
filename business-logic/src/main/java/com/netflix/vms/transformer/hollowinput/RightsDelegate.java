package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RightsDelegate extends HollowObjectDelegate {

    public int getWindowsOrdinal(int ordinal);

    public int getContractsOrdinal(int ordinal);

    public RightsTypeAPI getTypeAPI();

}