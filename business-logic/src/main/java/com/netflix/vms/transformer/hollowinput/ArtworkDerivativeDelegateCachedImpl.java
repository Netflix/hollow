package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ArtworkDerivativeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ArtworkDerivativeDelegate {

    private final int imageTypeOrdinal;
    private final int recipeNameOrdinal;
    private final Long width;
    private final Long height;
    private final int recipeDescriptorOrdinal;
    private final int cdnDirectoryOrdinal;
    private final int cdnIdOrdinal;
   private ArtworkDerivativeTypeAPI typeAPI;

    public ArtworkDerivativeDelegateCachedImpl(ArtworkDerivativeTypeAPI typeAPI, int ordinal) {
        this.imageTypeOrdinal = typeAPI.getImageTypeOrdinal(ordinal);
        this.recipeNameOrdinal = typeAPI.getRecipeNameOrdinal(ordinal);
        this.width = typeAPI.getWidthBoxed(ordinal);
        this.height = typeAPI.getHeightBoxed(ordinal);
        this.recipeDescriptorOrdinal = typeAPI.getRecipeDescriptorOrdinal(ordinal);
        this.cdnDirectoryOrdinal = typeAPI.getCdnDirectoryOrdinal(ordinal);
        this.cdnIdOrdinal = typeAPI.getCdnIdOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getImageTypeOrdinal(int ordinal) {
        return imageTypeOrdinal;
    }

    public int getRecipeNameOrdinal(int ordinal) {
        return recipeNameOrdinal;
    }

    public long getWidth(int ordinal) {
        return width.longValue();
    }

    public Long getWidthBoxed(int ordinal) {
        return width;
    }

    public long getHeight(int ordinal) {
        return height.longValue();
    }

    public Long getHeightBoxed(int ordinal) {
        return height;
    }

    public int getRecipeDescriptorOrdinal(int ordinal) {
        return recipeDescriptorOrdinal;
    }

    public int getCdnDirectoryOrdinal(int ordinal) {
        return cdnDirectoryOrdinal;
    }

    public int getCdnIdOrdinal(int ordinal) {
        return cdnIdOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ArtworkDerivativeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ArtworkDerivativeTypeAPI) typeAPI;
    }

}