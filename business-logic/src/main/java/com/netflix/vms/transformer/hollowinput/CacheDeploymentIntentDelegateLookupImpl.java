package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CacheDeploymentIntentDelegateLookupImpl extends HollowObjectAbstractDelegate implements CacheDeploymentIntentDelegate {

    private final CacheDeploymentIntentTypeAPI typeAPI;

    public CacheDeploymentIntentDelegateLookupImpl(CacheDeploymentIntentTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getStreamProfileId(int ordinal) {
        return typeAPI.getStreamProfileId(ordinal);
    }

    public Long getStreamProfileIdBoxed(int ordinal) {
        return typeAPI.getStreamProfileIdBoxed(ordinal);
    }

    public int getIsoCountryCodeOrdinal(int ordinal) {
        return typeAPI.getIsoCountryCodeOrdinal(ordinal);
    }

    public long getBitrateKBPS(int ordinal) {
        return typeAPI.getBitrateKBPS(ordinal);
    }

    public Long getBitrateKBPSBoxed(int ordinal) {
        return typeAPI.getBitrateKBPSBoxed(ordinal);
    }

    public CacheDeploymentIntentTypeAPI getTypeAPI() {
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