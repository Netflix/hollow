package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.custom.HollowListTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;

@SuppressWarnings("all")
public class ListOfDealCountryGroupTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    public ListOfDealCountryGroupTypeAPI(PackageDealCountryAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public DealCountryGroupTypeAPI getElementAPI() {
        return getAPI().getDealCountryGroupTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public PackageDealCountryAPI getAPI() {
        return (PackageDealCountryAPI)api;
    }

}