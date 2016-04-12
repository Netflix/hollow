package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowMapTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowMapLookupDelegate;

public class RolloutMapOfLaunchDatesTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    RolloutMapOfLaunchDatesTypeAPI(VMSHollowVideoInputAPI api, HollowMapTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowMapLookupDelegate(this);
    }

    public ISOCountryTypeAPI getKeyAPI() {
        return getAPI().getISOCountryTypeAPI();
    }

    public DateTypeAPI getValueAPI() {
        return getAPI().getDateTypeAPI();
    }

    public HollowMapLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI)api;
    }

}