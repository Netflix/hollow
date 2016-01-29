package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoRightsRightsArrayOfWindowsHollow extends HollowList<VideoRightsRightsWindowsHollow> {

    public VideoRightsRightsArrayOfWindowsHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoRightsRightsWindowsHollow instantiateElement(int ordinal) {
        return (VideoRightsRightsWindowsHollow) api().getVideoRightsRightsWindowsHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRightsRightsArrayOfWindowsTypeAPI typeApi() {
        return (VideoRightsRightsArrayOfWindowsTypeAPI) delegate.getTypeAPI();
    }

}