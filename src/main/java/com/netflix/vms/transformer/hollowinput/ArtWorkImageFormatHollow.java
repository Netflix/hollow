package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ArtWorkImageFormatHollow extends HollowObject {

    public ArtWorkImageFormatHollow(ArtWorkImageFormatDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getFormat() {
        int refOrdinal = delegate().getFormatOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getWidth() {
        return delegate().getWidth(ordinal);
    }

    public Long _getWidthBoxed() {
        return delegate().getWidthBoxed(ordinal);
    }

    public long _getHeight() {
        return delegate().getHeight(ordinal);
    }

    public Long _getHeightBoxed() {
        return delegate().getHeightBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ArtWorkImageFormatTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ArtWorkImageFormatDelegate delegate() {
        return (ArtWorkImageFormatDelegate)delegate;
    }

}