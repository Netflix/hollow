package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoRightsContractPackagesListHollow extends HollowList<VideoRightsContractPackageHollow> {

    public VideoRightsContractPackagesListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoRightsContractPackageHollow instantiateElement(int ordinal) {
        return (VideoRightsContractPackageHollow) api().getVideoRightsContractPackageHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsContractPackagesListTypeAPI typeApi() {
        return (VideoRightsContractPackagesListTypeAPI) delegate.getTypeAPI();
    }

}