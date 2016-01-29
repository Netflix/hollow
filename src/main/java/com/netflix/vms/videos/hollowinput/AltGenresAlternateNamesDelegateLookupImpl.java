package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class AltGenresAlternateNamesDelegateLookupImpl extends HollowObjectAbstractDelegate implements AltGenresAlternateNamesDelegate {

    private final AltGenresAlternateNamesTypeAPI typeAPI;

    public AltGenresAlternateNamesDelegateLookupImpl(AltGenresAlternateNamesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return typeAPI.getTranslatedTextsOrdinal(ordinal);
    }

    public long getTypeId(int ordinal) {
        return typeAPI.getTypeId(ordinal);
    }

    public Long getTypeIdBoxed(int ordinal) {
        return typeAPI.getTypeIdBoxed(ordinal);
    }

    public int getTypeOrdinal(int ordinal) {
        return typeAPI.getTypeOrdinal(ordinal);
    }

    public AltGenresAlternateNamesTypeAPI getTypeAPI() {
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