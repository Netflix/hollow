package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.custom.HollowListTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;

@SuppressWarnings("all")
public class SupplementalsListTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    public SupplementalsListTypeAPI(SupplementalAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public IndividualSupplementalTypeAPI getElementAPI() {
        return getAPI().getIndividualSupplementalTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public SupplementalAPI getAPI() {
        return (SupplementalAPI)api;
    }

}