package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class OriginServersDelegateLookupImpl extends HollowObjectAbstractDelegate implements OriginServersDelegate {

    private final OriginServersTypeAPI typeAPI;

    public OriginServersDelegateLookupImpl(OriginServersTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return typeAPI.getId(ordinal);
    }

    public Long getIdBoxed(int ordinal) {
        return typeAPI.getIdBoxed(ordinal);
    }

    public int getNameOrdinal(int ordinal) {
        return typeAPI.getNameOrdinal(ordinal);
    }

    public int getStorageGroupIdOrdinal(int ordinal) {
        return typeAPI.getStorageGroupIdOrdinal(ordinal);
    }

    public OriginServersTypeAPI getTypeAPI() {
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