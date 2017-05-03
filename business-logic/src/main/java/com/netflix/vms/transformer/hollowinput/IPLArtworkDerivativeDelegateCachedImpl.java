package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class IPLArtworkDerivativeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, IPLArtworkDerivativeDelegate {

    private final int recipeNameOrdinal;
    private final Integer widthInPixels;
    private final Integer heightInPixels;
    private final Integer targetWidthInPixels;
    private final Integer targetHeightInPixels;
    private final int recipeDescriptorOrdinal;
    private final int cdnIdOrdinal;
    private final int languageCodeOrdinal;
    private final int modificationsOrdinal;
    private final int overlayTypesOrdinal;
   private IPLArtworkDerivativeTypeAPI typeAPI;

    public IPLArtworkDerivativeDelegateCachedImpl(IPLArtworkDerivativeTypeAPI typeAPI, int ordinal) {
        this.recipeNameOrdinal = typeAPI.getRecipeNameOrdinal(ordinal);
        this.widthInPixels = typeAPI.getWidthInPixelsBoxed(ordinal);
        this.heightInPixels = typeAPI.getHeightInPixelsBoxed(ordinal);
        this.targetWidthInPixels = typeAPI.getTargetWidthInPixelsBoxed(ordinal);
        this.targetHeightInPixels = typeAPI.getTargetHeightInPixelsBoxed(ordinal);
        this.recipeDescriptorOrdinal = typeAPI.getRecipeDescriptorOrdinal(ordinal);
        this.cdnIdOrdinal = typeAPI.getCdnIdOrdinal(ordinal);
        this.languageCodeOrdinal = typeAPI.getLanguageCodeOrdinal(ordinal);
        this.modificationsOrdinal = typeAPI.getModificationsOrdinal(ordinal);
        this.overlayTypesOrdinal = typeAPI.getOverlayTypesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getRecipeNameOrdinal(int ordinal) {
        return recipeNameOrdinal;
    }

    public int getWidthInPixels(int ordinal) {
        return widthInPixels.intValue();
    }

    public Integer getWidthInPixelsBoxed(int ordinal) {
        return widthInPixels;
    }

    public int getHeightInPixels(int ordinal) {
        return heightInPixels.intValue();
    }

    public Integer getHeightInPixelsBoxed(int ordinal) {
        return heightInPixels;
    }

    public int getTargetWidthInPixels(int ordinal) {
        return targetWidthInPixels.intValue();
    }

    public Integer getTargetWidthInPixelsBoxed(int ordinal) {
        return targetWidthInPixels;
    }

    public int getTargetHeightInPixels(int ordinal) {
        return targetHeightInPixels.intValue();
    }

    public Integer getTargetHeightInPixelsBoxed(int ordinal) {
        return targetHeightInPixels;
    }

    public int getRecipeDescriptorOrdinal(int ordinal) {
        return recipeDescriptorOrdinal;
    }

    public int getCdnIdOrdinal(int ordinal) {
        return cdnIdOrdinal;
    }

    public int getLanguageCodeOrdinal(int ordinal) {
        return languageCodeOrdinal;
    }

    public int getModificationsOrdinal(int ordinal) {
        return modificationsOrdinal;
    }

    public int getOverlayTypesOrdinal(int ordinal) {
        return overlayTypesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public IPLArtworkDerivativeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (IPLArtworkDerivativeTypeAPI) typeAPI;
    }

}