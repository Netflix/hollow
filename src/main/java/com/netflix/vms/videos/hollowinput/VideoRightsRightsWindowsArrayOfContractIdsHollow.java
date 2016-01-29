package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoRightsRightsWindowsArrayOfContractIdsHollow extends HollowList<VideoRightsRightsWindowsContractIdsHollow> {

    public VideoRightsRightsWindowsArrayOfContractIdsHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoRightsRightsWindowsContractIdsHollow instantiateElement(int ordinal) {
        return (VideoRightsRightsWindowsContractIdsHollow) api().getVideoRightsRightsWindowsContractIdsHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsWindowsArrayOfContractIdsTypeAPI typeApi() {
        return (VideoRightsRightsWindowsArrayOfContractIdsTypeAPI) delegate.getTypeAPI();
    }

}