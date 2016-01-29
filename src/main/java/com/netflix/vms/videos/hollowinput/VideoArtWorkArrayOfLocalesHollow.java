package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoArtWorkArrayOfLocalesHollow extends HollowList<VideoArtWorkLocalesHollow> {

    public VideoArtWorkArrayOfLocalesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoArtWorkLocalesHollow instantiateElement(int ordinal) {
        return (VideoArtWorkLocalesHollow) api().getVideoArtWorkLocalesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkArrayOfLocalesTypeAPI typeApi() {
        return (VideoArtWorkArrayOfLocalesTypeAPI) delegate.getTypeAPI();
    }

}