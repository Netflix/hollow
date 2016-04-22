package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowListTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowListLookupDelegate;

public class ISOCountryListTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    ISOCountryListTypeAPI(VMSHollowInputAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public ISOCountryTypeAPI getElementAPI() {
        return getAPI().getISOCountryTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI)api;
    }

}