package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class VideoArtworkAttributeHollow extends HollowObject {

    public VideoArtworkAttributeHollow(VideoArtworkAttributeDelegate delegate, int ordinal) {
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

    public VideoArtworkAttributeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoArtworkAttributeDelegate delegate() {
        return (VideoArtworkAttributeDelegate)delegate;
    }

}