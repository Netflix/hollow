package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowMapTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowMapLookupDelegate;

public class RolloutPhaseWindowMapTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    RolloutPhaseWindowMapTypeAPI(VMSHollowInputAPI api, HollowMapTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowMapLookupDelegate(this);
    }

    public ISOCountryTypeAPI getKeyAPI() {
        return getAPI().getISOCountryTypeAPI();
    }

    public RolloutPhaseWindowTypeAPI getValueAPI() {
        return getAPI().getRolloutPhaseWindowTypeAPI();
    }

    public HollowMapLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI)api;
    }

}