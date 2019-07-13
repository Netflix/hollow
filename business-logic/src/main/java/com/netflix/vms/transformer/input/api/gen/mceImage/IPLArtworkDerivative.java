package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class IPLArtworkDerivative extends HollowObject {

    public IPLArtworkDerivative(IPLArtworkDerivativeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getRecipeName() {
        return delegate().getRecipeName(ordinal);
    }

    public boolean isRecipeNameEqual(String testValue) {
        return delegate().isRecipeNameEqual(ordinal, testValue);
    }

    public HString getRecipeNameHollowReference() {
        int refOrdinal = delegate().getRecipeNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public int getWidthInPixels() {
        return delegate().getWidthInPixels(ordinal);
    }

    public Integer getWidthInPixelsBoxed() {
        return delegate().getWidthInPixelsBoxed(ordinal);
    }

    public int getHeightInPixels() {
        return delegate().getHeightInPixels(ordinal);
    }

    public Integer getHeightInPixelsBoxed() {
        return delegate().getHeightInPixelsBoxed(ordinal);
    }

    public int getTargetWidthInPixels() {
        return delegate().getTargetWidthInPixels(ordinal);
    }

    public Integer getTargetWidthInPixelsBoxed() {
        return delegate().getTargetWidthInPixelsBoxed(ordinal);
    }

    public int getTargetHeightInPixels() {
        return delegate().getTargetHeightInPixels(ordinal);
    }

    public Integer getTargetHeightInPixelsBoxed() {
        return delegate().getTargetHeightInPixelsBoxed(ordinal);
    }

    public String getRecipeDescriptor() {
        return delegate().getRecipeDescriptor(ordinal);
    }

    public boolean isRecipeDescriptorEqual(String testValue) {
        return delegate().isRecipeDescriptorEqual(ordinal, testValue);
    }

    public HString getRecipeDescriptorHollowReference() {
        int refOrdinal = delegate().getRecipeDescriptorOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getCdnId() {
        return delegate().getCdnId(ordinal);
    }

    public boolean isCdnIdEqual(String testValue) {
        return delegate().isCdnIdEqual(ordinal, testValue);
    }

    public HString getCdnIdHollowReference() {
        int refOrdinal = delegate().getCdnIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getLanguageCode() {
        return delegate().getLanguageCode(ordinal);
    }

    public boolean isLanguageCodeEqual(String testValue) {
        return delegate().isLanguageCodeEqual(ordinal, testValue);
    }

    public HString getLanguageCodeHollowReference() {
        int refOrdinal = delegate().getLanguageCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public ListOfDerivativeTag getModifications() {
        int refOrdinal = delegate().getModificationsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfDerivativeTag(refOrdinal);
    }

    public ListOfDerivativeTag getOverlayTypes() {
        int refOrdinal = delegate().getOverlayTypesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfDerivativeTag(refOrdinal);
    }

    public MceImageV3API api() {
        return typeApi().getAPI();
    }

    public IPLArtworkDerivativeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected IPLArtworkDerivativeDelegate delegate() {
        return (IPLArtworkDerivativeDelegate)delegate;
    }

}