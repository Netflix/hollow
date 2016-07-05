package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoRatingArrayOfRatingHollow extends HollowList<VideoRatingRatingHollow> {

    public VideoRatingArrayOfRatingHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoRatingRatingHollow instantiateElement(int ordinal) {
        return (VideoRatingRatingHollow) api().getVideoRatingRatingHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRatingArrayOfRatingTypeAPI typeApi() {
        return (VideoRatingArrayOfRatingTypeAPI) delegate.getTypeAPI();
    }

}