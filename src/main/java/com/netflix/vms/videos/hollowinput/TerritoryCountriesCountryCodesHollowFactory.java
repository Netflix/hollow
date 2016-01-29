package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class TerritoryCountriesCountryCodesHollowFactory<T extends TerritoryCountriesCountryCodesHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new TerritoryCountriesCountryCodesHollow(((TerritoryCountriesCountryCodesTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new TerritoryCountriesCountryCodesHollow(new TerritoryCountriesCountryCodesDelegateCachedImpl((TerritoryCountriesCountryCodesTypeAPI)typeAPI, ordinal), ordinal);
    }

}