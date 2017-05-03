package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLArtworkDerivativeHollow extends HollowObject {

    public IPLArtworkDerivativeHollow(IPLArtworkDerivativeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getRecipeName() {
        int refOrdinal = delegate().getRecipeNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public int _getWidthInPixels() {
        return delegate().getWidthInPixels(ordinal);
    }

    public Integer _getWidthInPixelsBoxed() {
        return delegate().getWidthInPixelsBoxed(ordinal);
    }

    public int _getHeightInPixels() {
        return delegate().getHeightInPixels(ordinal);
    }

    public Integer _getHeightInPixelsBoxed() {
        return delegate().getHeightInPixelsBoxed(ordinal);
    }

    public int _getTargetWidthInPixels() {
        return delegate().getTargetWidthInPixels(ordinal);
    }

    public Integer _getTargetWidthInPixelsBoxed() {
        return delegate().getTargetWidthInPixelsBoxed(ordinal);
    }

    public int _getTargetHeightInPixels() {
        return delegate().getTargetHeightInPixels(ordinal);
    }

    public Integer _getTargetHeightInPixelsBoxed() {
        return delegate().getTargetHeightInPixelsBoxed(ordinal);
    }

    public StringHollow _getRecipeDescriptor() {
        int refOrdinal = delegate().getRecipeDescriptorOrdinal(ordinal);
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

    public StringHollow _getLanguageCode() {
        int refOrdinal = delegate().getLanguageCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public ListOfDerivativeTagHollow _getModifications() {
        int refOrdinal = delegate().getModificationsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfDerivativeTagHollow(refOrdinal);
    }

    public ListOfDerivativeTagHollow _getOverlayTypes() {
        int refOrdinal = delegate().getOverlayTypesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfDerivativeTagHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public IPLArtworkDerivativeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected IPLArtworkDerivativeDelegate delegate() {
        return (IPLArtworkDerivativeDelegate)delegate;
    }

}