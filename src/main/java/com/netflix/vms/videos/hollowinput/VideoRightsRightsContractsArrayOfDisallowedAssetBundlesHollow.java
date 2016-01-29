package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow extends HollowList<VideoRightsRightsContractsDisallowedAssetBundlesHollow> {

    public VideoRightsRightsContractsArrayOfDisallowedAssetBundlesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoRightsRightsContractsDisallowedAssetBundlesHollow instantiateElement(int ordinal) {
        return (VideoRightsRightsContractsDisallowedAssetBundlesHollow) api().getVideoRightsRightsContractsDisallowedAssetBundlesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI typeApi() {
        return (VideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI) delegate.getTypeAPI();
    }

}