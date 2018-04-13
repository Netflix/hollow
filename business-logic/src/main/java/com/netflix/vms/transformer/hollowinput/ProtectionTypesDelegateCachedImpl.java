package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ProtectionTypesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ProtectionTypesDelegate {

    private final int nameOrdinal;
    private final Long id;
    private ProtectionTypesTypeAPI typeAPI;

    public ProtectionTypesDelegateCachedImpl(ProtectionTypesTypeAPI typeAPI, int ordinal) {
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.id = typeAPI.getIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    public long getId(int ordinal) {
        if(id == null)
            return Long.MIN_VALUE;
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ProtectionTypesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ProtectionTypesTypeAPI) typeAPI;
    }

}