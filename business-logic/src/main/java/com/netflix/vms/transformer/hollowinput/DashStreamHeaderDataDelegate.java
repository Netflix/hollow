package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface DashStreamHeaderDataDelegate extends HollowObjectDelegate {

    public int getBoxInfoOrdinal(int ordinal);

    public DashStreamHeaderDataTypeAPI getTypeAPI();

}