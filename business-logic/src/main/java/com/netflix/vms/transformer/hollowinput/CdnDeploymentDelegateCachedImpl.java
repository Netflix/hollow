package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class CdnDeploymentDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CdnDeploymentDelegate {

    private final Long originServerId;
    private final int directoryOrdinal;
    private final int originServerOrdinal;
   private CdnDeploymentTypeAPI typeAPI;

    public CdnDeploymentDelegateCachedImpl(CdnDeploymentTypeAPI typeAPI, int ordinal) {
        this.originServerId = typeAPI.getOriginServerIdBoxed(ordinal);
        this.directoryOrdinal = typeAPI.getDirectoryOrdinal(ordinal);
        this.originServerOrdinal = typeAPI.getOriginServerOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getOriginServerId(int ordinal) {
        return originServerId.longValue();
    }

    public Long getOriginServerIdBoxed(int ordinal) {
        return originServerId;
    }

    public int getDirectoryOrdinal(int ordinal) {
        return directoryOrdinal;
    }

    public int getOriginServerOrdinal(int ordinal) {
        return originServerOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CdnDeploymentTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CdnDeploymentTypeAPI) typeAPI;
    }

}