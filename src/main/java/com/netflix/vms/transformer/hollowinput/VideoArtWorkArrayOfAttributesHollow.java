package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoArtWorkArrayOfAttributesHollow extends HollowList<VideoArtWorkAttributesHollow> {

    public VideoArtWorkArrayOfAttributesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoArtWorkAttributesHollow instantiateElement(int ordinal) {
        return (VideoArtWorkAttributesHollow) api().getVideoArtWorkAttributesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkArrayOfAttributesTypeAPI typeApi() {
        return (VideoArtWorkArrayOfAttributesTypeAPI) delegate.getTypeAPI();
    }

}