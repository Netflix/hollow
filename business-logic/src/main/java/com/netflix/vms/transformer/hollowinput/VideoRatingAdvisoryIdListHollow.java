package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoRatingAdvisoryIdListHollow extends HollowList<VideoRatingAdvisoryIdHollow> {

    public VideoRatingAdvisoryIdListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoRatingAdvisoryIdHollow instantiateElement(int ordinal) {
        return (VideoRatingAdvisoryIdHollow) api().getVideoRatingAdvisoryIdHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRatingAdvisoryIdListTypeAPI typeApi() {
        return (VideoRatingAdvisoryIdListTypeAPI) delegate.getTypeAPI();
    }

}