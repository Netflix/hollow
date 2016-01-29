package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowListTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowListLookupDelegate;

public class VideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    VideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI(VMSHollowVideoInputAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public VideoRightsRightsContractsDisallowedAssetBundlesTypeAPI getElementAPI() {
        return getAPI().getVideoRightsRightsContractsDisallowedAssetBundlesTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI)api;
    }

}