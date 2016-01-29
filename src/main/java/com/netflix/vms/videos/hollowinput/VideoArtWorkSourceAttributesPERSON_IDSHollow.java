package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkSourceAttributesPERSON_IDSHollow extends HollowObject {

    public VideoArtWorkSourceAttributesPERSON_IDSHollow(VideoArtWorkSourceAttributesPERSON_IDSDelegate delegate, int ordinal) {
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

    public VideoArtWorkSourceAttributesPERSON_IDSTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoArtWorkSourceAttributesPERSON_IDSDelegate delegate() {
        return (VideoArtWorkSourceAttributesPERSON_IDSDelegate)delegate;
    }

}