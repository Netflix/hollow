package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class TrailerThemeDelegateLookupImpl extends HollowObjectAbstractDelegate implements TrailerThemeDelegate {

    private final TrailerThemeTypeAPI typeAPI;

    public TrailerThemeDelegateLookupImpl(TrailerThemeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public TrailerThemeTypeAPI getTypeAPI() {
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