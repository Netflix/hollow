package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowSetTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowSetLookupDelegate;

public class VideoRightsWindowsSetTypeAPI extends HollowSetTypeAPI {

    private final HollowSetLookupDelegate delegateLookupImpl;

    VideoRightsWindowsSetTypeAPI(VMSHollowVideoInputAPI api, HollowSetTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowSetLookupDelegate(this);
    }

    public VideoRightsWindowTypeAPI getElementAPI() {
        return getAPI().getVideoRightsWindowTypeAPI();
    }

    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI)api;
    }

    public HollowSetLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

}