package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface TerritoryCountriesDelegate extends HollowObjectDelegate {

    public int getTerritoryCodeOrdinal(int ordinal);

    public int getCountryCodesOrdinal(int ordinal);

    public TerritoryCountriesTypeAPI getTypeAPI();

}