package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ArtworkDerivativeDelegateLookupImpl extends HollowObjectAbstractDelegate implements ArtworkDerivativeDelegate {

    private final ArtworkDerivativeTypeAPI typeAPI;

    public ArtworkDerivativeDelegateLookupImpl(ArtworkDerivativeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getImageTypeOrdinal(int ordinal) {
        return typeAPI.getImageTypeOrdinal(ordinal);
    }

    public int getRecipeNameOrdinal(int ordinal) {
        return typeAPI.getRecipeNameOrdinal(ordinal);
    }

    public long getWidth(int ordinal) {
        return typeAPI.getWidth(ordinal);
    }

    public Long getWidthBoxed(int ordinal) {
        return typeAPI.getWidthBoxed(ordinal);
    }

    public long getHeight(int ordinal) {
        return typeAPI.getHeight(ordinal);
    }

    public Long getHeightBoxed(int ordinal) {
        return typeAPI.getHeightBoxed(ordinal);
    }

    public int getRecipeDescriptorOrdinal(int ordinal) {
        return typeAPI.getRecipeDescriptorOrdinal(ordinal);
    }

    public int getCdnDirectoryOrdinal(int ordinal) {
        return typeAPI.getCdnDirectoryOrdinal(ordinal);
    }

    public int getCdnIdOrdinal(int ordinal) {
        return typeAPI.getCdnIdOrdinal(ordinal);
    }

    public ArtworkDerivativeTypeAPI getTypeAPI() {
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