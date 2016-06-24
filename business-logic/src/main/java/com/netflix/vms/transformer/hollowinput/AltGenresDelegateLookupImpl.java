package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class AltGenresDelegateLookupImpl extends HollowObjectAbstractDelegate implements AltGenresDelegate {

    private final AltGenresTypeAPI typeAPI;

    public AltGenresDelegateLookupImpl(AltGenresTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getAltGenreId(int ordinal) {
        return typeAPI.getAltGenreId(ordinal);
    }

    public Long getAltGenreIdBoxed(int ordinal) {
        return typeAPI.getAltGenreIdBoxed(ordinal);
    }

    public int getDisplayNameOrdinal(int ordinal) {
        return typeAPI.getDisplayNameOrdinal(ordinal);
    }

    public int getShortNameOrdinal(int ordinal) {
        return typeAPI.getShortNameOrdinal(ordinal);
    }

    public int getAlternateNamesOrdinal(int ordinal) {
        return typeAPI.getAlternateNamesOrdinal(ordinal);
    }

    public AltGenresTypeAPI getTypeAPI() {
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