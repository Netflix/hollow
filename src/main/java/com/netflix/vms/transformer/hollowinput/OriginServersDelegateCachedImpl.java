package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class OriginServersDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, OriginServersDelegate {

    private final Long id;
    private final int nameOrdinal;
    private final int storageGroupIdOrdinal;
   private OriginServersTypeAPI typeAPI;

    public OriginServersDelegateCachedImpl(OriginServersTypeAPI typeAPI, int ordinal) {
        this.id = typeAPI.getIdBoxed(ordinal);
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.storageGroupIdOrdinal = typeAPI.getStorageGroupIdOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
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

    public OriginServersTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (OriginServersTypeAPI) typeAPI;
    }

}