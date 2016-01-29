package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowListCachedDelegate;

public class VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollowFactory<T extends VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow(((VideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow(new HollowListCachedDelegate((VideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI)typeAPI, ordinal), ordinal);
    }

}