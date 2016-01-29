package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow extends HollowObject {

    public VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow(VideoArtWorkSourceAttributesAWARD_CAMPAIGNSDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoArtWorkSourceAttributesAWARD_CAMPAIGNSDelegate delegate() {
        return (VideoArtWorkSourceAttributesAWARD_CAMPAIGNSDelegate)delegate;
    }

}