package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoRatingArrayOfRatingHollow extends HollowList<VideoRatingRatingHollow> {

    public VideoRatingArrayOfRatingHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
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