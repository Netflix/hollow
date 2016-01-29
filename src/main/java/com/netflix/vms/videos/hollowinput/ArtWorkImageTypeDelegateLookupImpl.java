package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ArtWorkImageTypeDelegateLookupImpl extends HollowObjectAbstractDelegate implements ArtWorkImageTypeDelegate {

    private final ArtWorkImageTypeTypeAPI typeAPI;

    public ArtWorkImageTypeDelegateLookupImpl(ArtWorkImageTypeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getExtensionOrdinal(int ordinal) {
        return typeAPI.getExtensionOrdinal(ordinal);
    }

    public int getRecipeOrdinal(int ordinal) {
        return typeAPI.getRecipeOrdinal(ordinal);
    }

    public int getImageTypeOrdinal(int ordinal) {
        return typeAPI.getImageTypeOrdinal(ordinal);
    }

    public ArtWorkImageTypeTypeAPI getTypeAPI() {
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