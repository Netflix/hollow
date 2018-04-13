package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class OriginServerDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, OriginServerDelegate {

    private final Long id;
    private final int nameOrdinal;
    private final int storageGroupIdOrdinal;
    private OriginServerTypeAPI typeAPI;

    public OriginServerDelegateCachedImpl(OriginServerTypeAPI typeAPI, int ordinal) {
        this.id = typeAPI.getIdBoxed(ordinal);
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.storageGroupIdOrdinal = typeAPI.getStorageGroupIdOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        if(id == null)
            return Long.MIN_VALUE;
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    public int getStorageGroupIdOrdinal(int ordinal) {
        return storageGroupIdOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public OriginServerTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (OriginServerTypeAPI) typeAPI;
    }

}