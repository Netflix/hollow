package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowListTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowListLookupDelegate;

@SuppressWarnings("all")
public class VideoRatingArrayOfRatingTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    VideoRatingArrayOfRatingTypeAPI(VMSHollowInputAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public VideoRatingRatingTypeAPI getElementAPI() {
        return getAPI().getVideoRatingRatingTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI)api;
    }

}