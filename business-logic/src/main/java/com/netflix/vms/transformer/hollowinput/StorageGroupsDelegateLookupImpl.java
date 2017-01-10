package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StorageGroupsDelegateLookupImpl extends HollowObjectAbstractDelegate implements StorageGroupsDelegate {

    private final StorageGroupsTypeAPI typeAPI;

    public StorageGroupsDelegateLookupImpl(StorageGroupsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getIdOrdinal(int ordinal) {
        return typeAPI.getIdOrdinal(ordinal);
    }

    public long getCdnId(int ordinal) {
        return typeAPI.getCdnId(ordinal);
    }

    public Long getCdnIdBoxed(int ordinal) {
        return typeAPI.getCdnIdBoxed(ordinal);
    }

    public int getCountriesOrdinal(int ordinal) {
        return typeAPI.getCountriesOrdinal(ordinal);
    }

    public StorageGroupsTypeAPI getTypeAPI() {
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