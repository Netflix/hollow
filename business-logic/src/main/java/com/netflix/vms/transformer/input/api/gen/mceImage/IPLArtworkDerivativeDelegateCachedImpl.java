package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLArtworkDerivativeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, IPLArtworkDerivativeDelegate {

    private final String recipeName;
    private final int recipeNameOrdinal;
    private final Integer widthInPixels;
    private final Integer heightInPixels;
    private final Integer targetWidthInPixels;
    private final Integer targetHeightInPixels;
    private final String recipeDescriptor;
    private final int recipeDescriptorOrdinal;
    private final String cdnId;
    private final int cdnIdOrdinal;
    private final String languageCode;
    private final int languageCodeOrdinal;
    private final int modificationsOrdinal;
    private final int overlayTypesOrdinal;
    private IPLArtworkDerivativeTypeAPI typeAPI;

    public IPLArtworkDerivativeDelegateCachedImpl(IPLArtworkDerivativeTypeAPI typeAPI, int ordinal) {
        this.recipeNameOrdinal = typeAPI.getRecipeNameOrdinal(ordinal);
        int recipeNameTempOrdinal = recipeNameOrdinal;
        this.recipeName = recipeNameTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(recipeNameTempOrdinal);
        this.widthInPixels = typeAPI.getWidthInPixelsBoxed(ordinal);
        this.heightInPixels = typeAPI.getHeightInPixelsBoxed(ordinal);
        this.targetWidthInPixels = typeAPI.getTargetWidthInPixelsBoxed(ordinal);
        this.targetHeightInPixels = typeAPI.getTargetHeightInPixelsBoxed(ordinal);
        this.recipeDescriptorOrdinal = typeAPI.getRecipeDescriptorOrdinal(ordinal);
        int recipeDescriptorTempOrdinal = recipeDescriptorOrdinal;
        this.recipeDescriptor = recipeDescriptorTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(recipeDescriptorTempOrdinal);
        this.cdnIdOrdinal = typeAPI.getCdnIdOrdinal(ordinal);
        int cdnIdTempOrdinal = cdnIdOrdinal;
        this.cdnId = cdnIdTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(cdnIdTempOrdinal);
        this.languageCodeOrdinal = typeAPI.getLanguageCodeOrdinal(ordinal);
        int languageCodeTempOrdinal = languageCodeOrdinal;
        this.languageCode = languageCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(languageCodeTempOrdinal);
        this.modificationsOrdinal = typeAPI.getModificationsOrdinal(ordinal);
        this.overlayTypesOrdinal = typeAPI.getOverlayTypesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getRecipeName(int ordinal) {
        return recipeName;
    }

    public boolean isRecipeNameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return recipeName == null;
        return testValue.equals(recipeName);
    }

    public int getRecipeNameOrdinal(int ordinal) {
        return recipeNameOrdinal;
    }

    public int getWidthInPixels(int ordinal) {
        if(widthInPixels == null)
            return Integer.MIN_VALUE;
        return widthInPixels.intValue();
    }

    public Integer getWidthInPixelsBoxed(int ordinal) {
        return widthInPixels;
    }

    public int getHeightInPixels(int ordinal) {
        if(heightInPixels == null)
            return Integer.MIN_VALUE;
        return heightInPixels.intValue();
    }

    public Integer getHeightInPixelsBoxed(int ordinal) {
        return heightInPixels;
    }

    public int getTargetWidthInPixels(int ordinal) {
        if(targetWidthInPixels == null)
            return Integer.MIN_VALUE;
        return targetWidthInPixels.intValue();
    }

    public Integer getTargetWidthInPixelsBoxed(int ordinal) {
        return targetWidthInPixels;
    }

    public int getTargetHeightInPixels(int ordinal) {
        if(targetHeightInPixels == null)
            return Integer.MIN_VALUE;
        return targetHeightInPixels.intValue();
    }

    public Integer getTargetHeightInPixelsBoxed(int ordinal) {
        return targetHeightInPixels;
    }

    public String getRecipeDescriptor(int ordinal) {
        return recipeDescriptor;
    }

    public boolean isRecipeDescriptorEqual(int ordinal, String testValue) {
        if(testValue == null)
            return recipeDescriptor == null;
        return testValue.equals(recipeDescriptor);
    }

    public int getRecipeDescriptorOrdinal(int ordinal) {
        return recipeDescriptorOrdinal;
    }

    public String getCdnId(int ordinal) {
        return cdnId;
    }

    public boolean isCdnIdEqual(int ordinal, String testValue) {
        if(testValue == null)
            return cdnId == null;
        return testValue.equals(cdnId);
    }

    public int getCdnIdOrdinal(int ordinal) {
        return cdnIdOrdinal;
    }

    public String getLanguageCode(int ordinal) {
        return languageCode;
    }

    public boolean isLanguageCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return languageCode == null;
        return testValue.equals(languageCode);
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