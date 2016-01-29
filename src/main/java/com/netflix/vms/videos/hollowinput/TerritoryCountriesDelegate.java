package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface TerritoryCountriesDelegate extends HollowObjectDelegate {

    public int getCountryCodesOrdinal(int ordinal);

    public int getTerritoryCodeOrdinal(int ordinal);

    public TerritoryCountriesTypeAPI getTypeAPI();

}