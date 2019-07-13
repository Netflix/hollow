package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLArtworkDerivativeDelegateLookupImpl extends HollowObjectAbstractDelegate implements IPLArtworkDerivativeDelegate {

    private final IPLArtworkDerivativeTypeAPI typeAPI;

    public IPLArtworkDerivativeDelegateLookupImpl(IPLArtworkDerivativeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getRecipeName(int ordinal) {
        ordinal = typeAPI.getRecipeNameOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isRecipeNameEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getRecipeNameOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getRecipeNameOrdinal(int ordinal) {
        return typeAPI.getRecipeNameOrdinal(ordinal);
    }

    public int getWidthInPixels(int ordinal) {
        return typeAPI.getWidthInPixels(ordinal);
    }

    public Integer getWidthInPixelsBoxed(int ordinal) {
        return typeAPI.getWidthInPixelsBoxed(ordinal);
    }

    public int getHeightInPixels(int ordinal) {
        return typeAPI.getHeightInPixels(ordinal);
    }

    public Integer getHeightInPixelsBoxed(int ordinal) {
        return typeAPI.getHeightInPixelsBoxed(ordinal);
    }

    public int getTargetWidthInPixels(int ordinal) {
        return typeAPI.getTargetWidthInPixels(ordinal);
    }

    public Integer getTargetWidthInPixelsBoxed(int ordinal) {
        return typeAPI.getTargetWidthInPixelsBoxed(ordinal);
    }

    public int getTargetHeightInPixels(int ordinal) {
        return typeAPI.getTargetHeightInPixels(ordinal);
    }

    public Integer getTargetHeightInPixelsBoxed(int ordinal) {
        return typeAPI.getTargetHeightInPixelsBoxed(ordinal);
    }

    public String getRecipeDescriptor(int ordinal) {
        ordinal = typeAPI.getRecipeDescriptorOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isRecipeDescriptorEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getRecipeDescriptorOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getRecipeDescriptorOrdinal(int ordinal) {
        return typeAPI.getRecipeDescriptorOrdinal(ordinal);
    }

    public String getCdnId(int ordinal) {
        ordinal = typeAPI.getCdnIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCdnIdEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCdnIdOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCdnIdOrdinal(int ordinal) {
        return typeAPI.getCdnIdOrdinal(ordinal);
    }

    public String getLanguageCode(int ordinal) {
        ordinal = typeAPI.getLanguageCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isLanguageCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getLanguageCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getLanguageCodeOrdinal(int ordinal) {
        return typeAPI.getLanguageCodeOrdinal(ordinal);
    }

    public int getModificationsOrdinal(int ordinal) {
        return typeAPI.getModificationsOrdinal(ordinal);
    }

    public int getOverlayTypesOrdinal(int ordinal) {
        return typeAPI.getOverlayTypesOrdinal(ordinal);
    }

    public IPLArtworkDerivativeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}