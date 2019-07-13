package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowMapTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;

@SuppressWarnings("all")
public class RolloutPhaseWindowMapTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    public RolloutPhaseWindowMapTypeAPI(RolloutAPI api, HollowMapTypeDataAccess dataAccess) {
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

    public RolloutAPI getAPI() {
        return (RolloutAPI)api;
    }

}