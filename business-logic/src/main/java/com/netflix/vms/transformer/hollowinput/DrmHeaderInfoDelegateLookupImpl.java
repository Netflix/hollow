package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class DrmHeaderInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements DrmHeaderInfoDelegate {

    private final DrmHeaderInfoTypeAPI typeAPI;

    public DrmHeaderInfoDelegateLookupImpl(DrmHeaderInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getKeyIdOrdinal(int ordinal) {
        return typeAPI.getKeyIdOrdinal(ordinal);
    }

    public long getDrmSystemId(int ordinal) {
        return typeAPI.getDrmSystemId(ordinal);
    }

    public Long getDrmSystemIdBoxed(int ordinal) {
        return typeAPI.getDrmSystemIdBoxed(ordinal);
    }

    public int getChecksumOrdinal(int ordinal) {
        return typeAPI.getChecksumOrdinal(ordinal);
    }

    public DrmHeaderInfoTypeAPI getTypeAPI() {
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