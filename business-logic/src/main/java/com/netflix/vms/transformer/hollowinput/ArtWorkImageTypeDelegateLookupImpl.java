package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ArtWorkImageTypeDelegateLookupImpl extends HollowObjectAbstractDelegate implements ArtWorkImageTypeDelegate {

    private final ArtWorkImageTypeTypeAPI typeAPI;

    public ArtWorkImageTypeDelegateLookupImpl(ArtWorkImageTypeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getImageTypeOrdinal(int ordinal) {
        return typeAPI.getImageTypeOrdinal(ordinal);
    }

    public int getExtensionOrdinal(int ordinal) {
        return typeAPI.getExtensionOrdinal(ordinal);
    }

    public int getRecipeOrdinal(int ordinal) {
        return typeAPI.getRecipeOrdinal(ordinal);
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