package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class CdnDeploymentDelegateLookupImpl extends HollowObjectAbstractDelegate implements CdnDeploymentDelegate {

    private final CdnDeploymentTypeAPI typeAPI;

    public CdnDeploymentDelegateLookupImpl(CdnDeploymentTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getOriginServerId(int ordinal) {
        return typeAPI.getOriginServerId(ordinal);
    }

    public Long getOriginServerIdBoxed(int ordinal) {
        return typeAPI.getOriginServerIdBoxed(ordinal);
    }

    public int getDirectoryOrdinal(int ordinal) {
        return typeAPI.getDirectoryOrdinal(ordinal);
    }

    public int getOriginServerOrdinal(int ordinal) {
        return typeAPI.getOriginServerOrdinal(ordinal);
    }

    public CdnDeploymentTypeAPI getTypeAPI() {
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