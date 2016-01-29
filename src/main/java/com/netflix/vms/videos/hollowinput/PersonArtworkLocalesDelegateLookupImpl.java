package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class PersonArtworkLocalesDelegateLookupImpl extends HollowObjectAbstractDelegate implements PersonArtworkLocalesDelegate {

    private final PersonArtworkLocalesTypeAPI typeAPI;

    public PersonArtworkLocalesDelegateLookupImpl(PersonArtworkLocalesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getTerritoryCodesOrdinal(int ordinal) {
        return typeAPI.getTerritoryCodesOrdinal(ordinal);
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        return typeAPI.getBcp47CodeOrdinal(ordinal);
    }

    public long getEffectiveDate(int ordinal) {
        return typeAPI.getEffectiveDate(ordinal);
    }

    public Long getEffectiveDateBoxed(int ordinal) {
        return typeAPI.getEffectiveDateBoxed(ordinal);
    }

    public PersonArtworkLocalesTypeAPI getTypeAPI() {
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