package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class StreamDeploymentInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamDeploymentInfoDelegate {

    private final int cacheDeployedCountriesOrdinal;
    private final int cdnDeploymentsOrdinal;
   private StreamDeploymentInfoTypeAPI typeAPI;

    public StreamDeploymentInfoDelegateCachedImpl(StreamDeploymentInfoTypeAPI typeAPI, int ordinal) {
        this.cacheDeployedCountriesOrdinal = typeAPI.getCacheDeployedCountriesOrdinal(ordinal);
        this.cdnDeploymentsOrdinal = typeAPI.getCdnDeploymentsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCacheDeployedCountriesOrdinal(int ordinal) {
        return cacheDeployedCountriesOrdinal;
    }

    public int getCdnDeploymentsOrdinal(int ordinal) {
        return cdnDeploymentsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StreamDeploymentInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamDeploymentInfoTypeAPI) typeAPI;
    }

}