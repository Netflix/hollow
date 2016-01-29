package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow extends HollowList<VideoArtWorkSourceAttributesPERSON_IDSHollow> {

    public VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoArtWorkSourceAttributesPERSON_IDSHollow instantiateElement(int ordinal) {
        return (VideoArtWorkSourceAttributesPERSON_IDSHollow) api().getVideoArtWorkSourceAttributesPERSON_IDSHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI typeApi() {
        return (VideoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI) delegate.getTypeAPI();
    }

}