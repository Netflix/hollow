package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface PassthroughDataDelegate extends HollowObjectDelegate {

    public int getSingleValuesOrdinal(int ordinal);

    public int getMultiValuesOrdinal(int ordinal);

    public PassthroughDataTypeAPI getTypeAPI();

}