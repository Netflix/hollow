package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class StreamDeploymentInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements StreamDeploymentInfoDelegate {

    private final StreamDeploymentInfoTypeAPI typeAPI;

    public StreamDeploymentInfoDelegateLookupImpl(StreamDeploymentInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getCacheDeployedCountriesOrdinal(int ordinal) {
        return typeAPI.getCacheDeployedCountriesOrdinal(ordinal);
    }

    public int getCdnDeploymentsOrdinal(int ordinal) {
        return typeAPI.getCdnDeploymentsOrdinal(ordinal);
    }

    public StreamDeploymentInfoTypeAPI getTypeAPI() {
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