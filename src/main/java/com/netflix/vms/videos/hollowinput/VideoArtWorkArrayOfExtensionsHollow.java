package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoArtWorkArrayOfExtensionsHollow extends HollowList<VideoArtWorkExtensionsHollow> {

    public VideoArtWorkArrayOfExtensionsHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoArtWorkExtensionsHollow instantiateElement(int ordinal) {
        return (VideoArtWorkExtensionsHollow) api().getVideoArtWorkExtensionsHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkArrayOfExtensionsTypeAPI typeApi() {
        return (VideoArtWorkArrayOfExtensionsTypeAPI) delegate.getTypeAPI();
    }

}