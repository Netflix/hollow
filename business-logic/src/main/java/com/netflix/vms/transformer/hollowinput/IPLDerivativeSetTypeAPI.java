package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowSetTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;

@SuppressWarnings("all")
public class IPLDerivativeSetTypeAPI extends HollowSetTypeAPI {

    private final HollowSetLookupDelegate delegateLookupImpl;

    public IPLDerivativeSetTypeAPI(VMSHollowInputAPI api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public IPLArtworkDerivativeTypeAPI getElementAPI() {
        return getAPI().getIPLArtworkDerivativeTypeAPI();
    }

    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI)api;
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

}