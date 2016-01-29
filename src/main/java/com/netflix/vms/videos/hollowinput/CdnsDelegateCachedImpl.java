package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CdnsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CdnsDelegate {

    private final int nameOrdinal;
    private final Long id;
   private CdnsTypeAPI typeAPI;

    public CdnsDelegateCachedImpl(CdnsTypeAPI typeAPI, int ordinal) {
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.id = typeAPI.getIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    public long getId(int ordinal) {
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

    public CdnsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CdnsTypeAPI) typeAPI;
    }

}