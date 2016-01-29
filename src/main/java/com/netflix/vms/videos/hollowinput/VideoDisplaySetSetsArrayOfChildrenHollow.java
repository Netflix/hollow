package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoDisplaySetSetsArrayOfChildrenHollow extends HollowList<VideoDisplaySetSetsChildrenHollow> {

    public VideoDisplaySetSetsArrayOfChildrenHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoDisplaySetSetsChildrenHollow instantiateElement(int ordinal) {
        return (VideoDisplaySetSetsChildrenHollow) api().getVideoDisplaySetSetsChildrenHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoDisplaySetSetsArrayOfChildrenTypeAPI typeApi() {
        return (VideoDisplaySetSetsArrayOfChildrenTypeAPI) delegate.getTypeAPI();
    }

}