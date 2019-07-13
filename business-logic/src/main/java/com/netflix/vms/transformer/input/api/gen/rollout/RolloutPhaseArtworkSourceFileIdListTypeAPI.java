package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowListTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;

@SuppressWarnings("all")
public class RolloutPhaseArtworkSourceFileIdListTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    public RolloutPhaseArtworkSourceFileIdListTypeAPI(RolloutAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public RolloutPhaseArtworkSourceFileIdTypeAPI getElementAPI() {
        return getAPI().getRolloutPhaseArtworkSourceFileIdTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public RolloutAPI getAPI() {
        return (RolloutAPI)api;
    }

}