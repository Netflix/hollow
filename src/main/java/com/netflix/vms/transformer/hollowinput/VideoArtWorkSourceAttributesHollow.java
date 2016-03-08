package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtWorkSourceAttributesHollow extends HollowObject {

    public VideoArtWorkSourceAttributesHollow(VideoArtWorkSourceAttributesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public PassthroughDataHollow _getPassthrough() {
        int refOrdinal = delegate().getPassthroughOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPassthroughDataHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkSourceAttributesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoArtWorkSourceAttributesDelegate delegate() {
        return (VideoArtWorkSourceAttributesDelegate)delegate;
    }

}