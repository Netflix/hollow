package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoRightsContractPackagesListHollow extends HollowList<VideoRightsContractPackageHollow> {

    public VideoRightsContractPackagesListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoRightsContractPackageHollow instantiateElement(int ordinal) {
        return (VideoRightsContractPackageHollow) api().getVideoRightsContractPackageHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsContractPackagesListTypeAPI typeApi() {
        return (VideoRightsContractPackagesListTypeAPI) delegate.getTypeAPI();
    }

}