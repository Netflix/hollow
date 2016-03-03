package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ArtworkAttributesHollow extends HollowObject {

    public ArtworkAttributesHollow(ArtworkAttributesDelegate delegate, int ordinal) {
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

    public ArtworkAttributesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ArtworkAttributesDelegate delegate() {
        return (ArtworkAttributesDelegate)delegate;
    }

}