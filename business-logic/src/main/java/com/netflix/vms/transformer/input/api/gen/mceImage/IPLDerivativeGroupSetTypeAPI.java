package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.custom.HollowSetTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;

@SuppressWarnings("all")
public class IPLDerivativeGroupSetTypeAPI extends HollowSetTypeAPI {

    private final HollowSetLookupDelegate delegateLookupImpl;

    public IPLDerivativeGroupSetTypeAPI(MceImageV3API api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public IPLDerivativeGroupTypeAPI getElementAPI() {
        return getAPI().getIPLDerivativeGroupTypeAPI();
    }

    public MceImageV3API getAPI() {
        return (MceImageV3API)api;
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

}