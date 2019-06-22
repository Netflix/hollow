package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowListTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;

@SuppressWarnings("all")
public class ListOfRightsContractPackageTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    public ListOfRightsContractPackageTypeAPI(Gk2StatusAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public RightsContractPackageTypeAPI getElementAPI() {
        return getAPI().getRightsContractPackageTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public Gk2StatusAPI getAPI() {
        return (Gk2StatusAPI)api;
    }

}