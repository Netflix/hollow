package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoArtWorkSourceAttributesArrayOfThemesHollow extends HollowList<VideoArtWorkSourceAttributesThemesHollow> {

    public VideoArtWorkSourceAttributesArrayOfThemesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoArtWorkSourceAttributesThemesHollow instantiateElement(int ordinal) {
        return (VideoArtWorkSourceAttributesThemesHollow) api().getVideoArtWorkSourceAttributesThemesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkSourceAttributesArrayOfThemesTypeAPI typeApi() {
        return (VideoArtWorkSourceAttributesArrayOfThemesTypeAPI) delegate.getTypeAPI();
    }

}