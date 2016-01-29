package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoRightsRightsContractsArrayOfAssetsHollow extends HollowList<VideoRightsRightsContractsAssetsHollow> {

    public VideoRightsRightsContractsArrayOfAssetsHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoRightsRightsContractsAssetsHollow instantiateElement(int ordinal) {
        return (VideoRightsRightsContractsAssetsHollow) api().getVideoRightsRightsContractsAssetsHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsContractsArrayOfAssetsTypeAPI typeApi() {
        return (VideoRightsRightsContractsArrayOfAssetsTypeAPI) delegate.getTypeAPI();
    }

}