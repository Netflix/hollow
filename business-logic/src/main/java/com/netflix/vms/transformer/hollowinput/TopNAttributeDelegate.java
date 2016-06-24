package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface TopNAttributeDelegate extends HollowObjectDelegate {

    public int getCountryOrdinal(int ordinal);

    public int getViewShareOrdinal(int ordinal);

    public int getCountryViewHrsOrdinal(int ordinal);

    public TopNAttributeTypeAPI getTypeAPI();

}