package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CountryVideoDisplaySetDelegate extends HollowObjectDelegate {

    public int getCountryCodeOrdinal(int ordinal);

    public int getSetTypeOrdinal(int ordinal);

    public int getChildrenOrdinal(int ordinal);

    public CountryVideoDisplaySetTypeAPI getTypeAPI();

}