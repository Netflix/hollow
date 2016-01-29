package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow extends HollowList<VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow> {

    public VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow instantiateElement(int ordinal) {
        return (VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow) api().getVideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI typeApi() {
        return (VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI) delegate.getTypeAPI();
    }

}