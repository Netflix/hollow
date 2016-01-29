package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoRightsRightsContractsArrayOfPackagesHollow extends HollowList<VideoRightsRightsContractsPackagesHollow> {

    public VideoRightsRightsContractsArrayOfPackagesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoRightsRightsContractsPackagesHollow instantiateElement(int ordinal) {
        return (VideoRightsRightsContractsPackagesHollow) api().getVideoRightsRightsContractsPackagesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsContractsArrayOfPackagesTypeAPI typeApi() {
        return (VideoRightsRightsContractsArrayOfPackagesTypeAPI) delegate.getTypeAPI();
    }

}