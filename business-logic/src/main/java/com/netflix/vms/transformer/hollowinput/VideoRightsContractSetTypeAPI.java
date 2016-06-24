package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowSetTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowSetLookupDelegate;

@SuppressWarnings("all")
public class VideoRightsContractSetTypeAPI extends HollowSetTypeAPI {

    private final HollowSetLookupDelegate delegateLookupImpl;

    VideoRightsContractSetTypeAPI(VMSHollowInputAPI api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public VideoRightsContractTypeAPI getElementAPI() {
        return getAPI().getVideoRightsContractTypeAPI();
    }

    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI)api;
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

}