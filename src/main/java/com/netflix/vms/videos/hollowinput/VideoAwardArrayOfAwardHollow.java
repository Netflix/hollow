package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoAwardArrayOfAwardHollow extends HollowList<VideoAwardAwardHollow> {

    public VideoAwardArrayOfAwardHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoAwardAwardHollow instantiateElement(int ordinal) {
        return (VideoAwardAwardHollow) api().getVideoAwardAwardHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoAwardArrayOfAwardTypeAPI typeApi() {
        return (VideoAwardArrayOfAwardTypeAPI) delegate.getTypeAPI();
    }

}