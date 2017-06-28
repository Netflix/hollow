package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ArtworkLocaleDelegateLookupImpl extends HollowObjectAbstractDelegate implements ArtworkLocaleDelegate {

    private final ArtworkLocaleTypeAPI typeAPI;

    public ArtworkLocaleDelegateLookupImpl(ArtworkLocaleTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getTerritoryCodesOrdinal(int ordinal) {
        return typeAPI.getTerritoryCodesOrdinal(ordinal);
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        return typeAPI.getBcp47CodeOrdinal(ordinal);
    }

    public int getEffectiveDateOrdinal(int ordinal) {
        return typeAPI.getEffectiveDateOrdinal(ordinal);
    }

    public int getAttributesOrdinal(int ordinal) {
        return typeAPI.getAttributesOrdinal(ordinal);
    }

    public ArtworkLocaleTypeAPI getTypeAPI() {
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