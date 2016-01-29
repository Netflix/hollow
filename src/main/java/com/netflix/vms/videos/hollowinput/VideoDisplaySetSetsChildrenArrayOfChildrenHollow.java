package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoDisplaySetSetsChildrenArrayOfChildrenHollow extends HollowList<VideoDisplaySetSetsChildrenChildrenHollow> {

    public VideoDisplaySetSetsChildrenArrayOfChildrenHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoDisplaySetSetsChildrenChildrenHollow instantiateElement(int ordinal) {
        return (VideoDisplaySetSetsChildrenChildrenHollow) api().getVideoDisplaySetSetsChildrenChildrenHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoDisplaySetSetsChildrenArrayOfChildrenTypeAPI typeApi() {
        return (VideoDisplaySetSetsChildrenArrayOfChildrenTypeAPI) delegate.getTypeAPI();
    }

}