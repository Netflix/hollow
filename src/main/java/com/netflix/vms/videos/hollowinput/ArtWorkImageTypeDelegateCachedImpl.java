package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class ArtWorkImageTypeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ArtWorkImageTypeDelegate {

    private final int extensionOrdinal;
    private final int recipeOrdinal;
    private final int imageTypeOrdinal;
   private ArtWorkImageTypeTypeAPI typeAPI;

    public ArtWorkImageTypeDelegateCachedImpl(ArtWorkImageTypeTypeAPI typeAPI, int ordinal) {
        this.extensionOrdinal = typeAPI.getExtensionOrdinal(ordinal);
        this.recipeOrdinal = typeAPI.getRecipeOrdinal(ordinal);
        this.imageTypeOrdinal = typeAPI.getImageTypeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getExtensionOrdinal(int ordinal) {
        return extensionOrdinal;
    }

    public int getRecipeOrdinal(int ordinal) {
        return recipeOrdinal;
    }

    public int getImageTypeOrdinal(int ordinal) {
        return imageTypeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ArtWorkImageTypeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ArtWorkImageTypeTypeAPI) typeAPI;
    }

}