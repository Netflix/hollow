package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class DrmHeaderInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DrmHeaderInfoDelegate {

    private final int keyIdOrdinal;
    private final Long drmSystemId;
    private final int checksumOrdinal;
   private DrmHeaderInfoTypeAPI typeAPI;

    public DrmHeaderInfoDelegateCachedImpl(DrmHeaderInfoTypeAPI typeAPI, int ordinal) {
        this.keyIdOrdinal = typeAPI.getKeyIdOrdinal(ordinal);
        this.drmSystemId = typeAPI.getDrmSystemIdBoxed(ordinal);
        this.checksumOrdinal = typeAPI.getChecksumOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getKeyIdOrdinal(int ordinal) {
        return keyIdOrdinal;
    }

    public long getDrmSystemId(int ordinal) {
        return drmSystemId.longValue();
    }

    public Long getDrmSystemIdBoxed(int ordinal) {
        return drmSystemId;
    }

    public int getChecksumOrdinal(int ordinal) {
        return checksumOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public DrmHeaderInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (DrmHeaderInfoTypeAPI) typeAPI;
    }

}