package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoPersonArrayOfCastHollow extends HollowList<VideoPersonCastHollow> {

    public VideoPersonArrayOfCastHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoPersonCastHollow instantiateElement(int ordinal) {
        return (VideoPersonCastHollow) api().getVideoPersonCastHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoPersonArrayOfCastTypeAPI typeApi() {
        return (VideoPersonArrayOfCastTypeAPI) delegate.getTypeAPI();
    }

}