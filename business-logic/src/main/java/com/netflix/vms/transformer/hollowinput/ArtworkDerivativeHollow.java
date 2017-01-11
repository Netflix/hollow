package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class ArtworkDerivativeHollow extends HollowObject {

    public ArtworkDerivativeHollow(ArtworkDerivativeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getImageType() {
        int refOrdinal = delegate().getImageTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getRecipeName() {
        int refOrdinal = delegate().getRecipeNameOrdinal(ordinal);
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

    public StringHollow _getRecipeDescriptor() {
        int refOrdinal = delegate().getRecipeDescriptorOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCdnDirectory() {
        int refOrdinal = delegate().getCdnDirectoryOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCdnId() {
        int refOrdinal = delegate().getCdnIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ArtworkDerivativeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ArtworkDerivativeDelegate delegate() {
        return (ArtworkDerivativeDelegate)delegate;
    }

}