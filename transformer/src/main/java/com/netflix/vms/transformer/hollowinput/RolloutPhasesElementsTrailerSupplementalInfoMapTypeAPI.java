package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowMapTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowMapLookupDelegate;

public class RolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    RolloutPhasesElementsTrailerSupplementalInfoMapTypeAPI(VMSHollowVideoInputAPI api, HollowMapTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowMapLookupDelegate(this);
    }

    public MapKeyTypeAPI getKeyAPI() {
        return getAPI().getMapKeyTypeAPI();
    }

    public RolloutPhaseTrailerSupplementalInfoTypeAPI getValueAPI() {
        return getAPI().getRolloutPhaseTrailerSupplementalInfoTypeAPI();
    }

    public HollowMapLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI)api;
    }

}