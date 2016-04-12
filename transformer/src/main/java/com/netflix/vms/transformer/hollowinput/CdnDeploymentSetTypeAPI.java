package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowSetTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowSetLookupDelegate;

public class CdnDeploymentSetTypeAPI extends HollowSetTypeAPI {

    private final HollowSetLookupDelegate delegateLookupImpl;

    CdnDeploymentSetTypeAPI(VMSHollowVideoInputAPI api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public CdnDeploymentTypeAPI getElementAPI() {
        return getAPI().getCdnDeploymentTypeAPI();
    }

    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI)api;
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

}