package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoArtWorkSourceAttributesArrayOfIDENTIFIERSHollow extends HollowList<VideoArtWorkSourceAttributesIDENTIFIERSHollow> {

    public VideoArtWorkSourceAttributesArrayOfIDENTIFIERSHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoArtWorkSourceAttributesIDENTIFIERSHollow instantiateElement(int ordinal) {
        return (VideoArtWorkSourceAttributesIDENTIFIERSHollow) api().getVideoArtWorkSourceAttributesIDENTIFIERSHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI typeApi() {
        return (VideoArtWorkSourceAttributesArrayOfIDENTIFIERSTypeAPI) delegate.getTypeAPI();
    }

}