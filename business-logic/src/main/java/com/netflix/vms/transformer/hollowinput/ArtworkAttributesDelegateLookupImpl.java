package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ArtworkAttributesDelegateLookupImpl extends HollowObjectAbstractDelegate implements ArtworkAttributesDelegate {

    private final ArtworkAttributesTypeAPI typeAPI;

    public ArtworkAttributesDelegateLookupImpl(ArtworkAttributesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getPassthroughOrdinal(int ordinal) {
        return typeAPI.getPassthroughOrdinal(ordinal);
    }

    public ArtworkAttributesTypeAPI getTypeAPI() {
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