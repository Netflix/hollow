package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

@SuppressWarnings("all")
public class PackageMovieDealCountryGroupHollowFactory<T extends PackageMovieDealCountryGroup> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new PackageMovieDealCountryGroup(((PackageMovieDealCountryGroupTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new PackageMovieDealCountryGroup(new PackageMovieDealCountryGroupDelegateCachedImpl((PackageMovieDealCountryGroupTypeAPI)typeAPI, ordinal), ordinal);
    }

}