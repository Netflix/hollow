package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowListTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowListLookupDelegate;

@SuppressWarnings("all")
public class ConsolidatedCertSystemRatingListTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    ConsolidatedCertSystemRatingListTypeAPI(VMSHollowInputAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public ConsolidatedCertSystemRatingTypeAPI getElementAPI() {
        return getAPI().getConsolidatedCertSystemRatingTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI)api;
    }

}