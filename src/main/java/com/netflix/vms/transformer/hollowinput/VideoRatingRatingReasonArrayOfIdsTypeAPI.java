package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowListTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowListLookupDelegate;

public class VideoRatingRatingReasonArrayOfIdsTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    VideoRatingRatingReasonArrayOfIdsTypeAPI(VMSHollowVideoInputAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public VideoRatingRatingReasonIdsTypeAPI getElementAPI() {
        return getAPI().getVideoRatingRatingReasonIdsTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI)api;
    }

}