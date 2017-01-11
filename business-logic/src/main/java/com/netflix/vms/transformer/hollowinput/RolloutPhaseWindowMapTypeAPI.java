package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowMapTypeAPI;

import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;

@SuppressWarnings("all")
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