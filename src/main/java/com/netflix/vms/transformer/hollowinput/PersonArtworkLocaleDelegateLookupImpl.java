package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class PersonArtworkLocaleDelegateLookupImpl extends HollowObjectAbstractDelegate implements PersonArtworkLocaleDelegate {

    private final PersonArtworkLocaleTypeAPI typeAPI;

    public PersonArtworkLocaleDelegateLookupImpl(PersonArtworkLocaleTypeAPI typeAPI) {
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

    public PersonArtworkLocaleTypeAPI getTypeAPI() {
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