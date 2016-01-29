package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoRightsRightsArrayOfContractsHollow extends HollowList<VideoRightsRightsContractsHollow> {

    public VideoRightsRightsArrayOfContractsHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoRightsRightsContractsHollow instantiateElement(int ordinal) {
        return (VideoRightsRightsContractsHollow) api().getVideoRightsRightsContractsHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsArrayOfContractsTypeAPI typeApi() {
        return (VideoRightsRightsArrayOfContractsTypeAPI) delegate.getTypeAPI();
    }

}