package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class ArtworkRecipeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ArtworkRecipeDelegate {

    private final int recipeNameOrdinal;
    private final int cdnFolderOrdinal;
    private final int extensionOrdinal;
    private final int hostNameOrdinal;
   private ArtworkRecipeTypeAPI typeAPI;

    public ArtworkRecipeDelegateCachedImpl(ArtworkRecipeTypeAPI typeAPI, int ordinal) {
        this.recipeNameOrdinal = typeAPI.getRecipeNameOrdinal(ordinal);
        this.cdnFolderOrdinal = typeAPI.getCdnFolderOrdinal(ordinal);
        this.extensionOrdinal = typeAPI.getExtensionOrdinal(ordinal);
        this.hostNameOrdinal = typeAPI.getHostNameOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getRecipeNameOrdinal(int ordinal) {
        return recipeNameOrdinal;
    }

    public int getCdnFolderOrdinal(int ordinal) {
        return cdnFolderOrdinal;
    }

    public int getExtensionOrdinal(int ordinal) {
        return extensionOrdinal;
    }

    public int getHostNameOrdinal(int ordinal) {
        return hostNameOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ArtworkRecipeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ArtworkRecipeTypeAPI) typeAPI;
    }

}