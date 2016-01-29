package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CacheDeploymentIntentDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CacheDeploymentIntentDelegate {

    private final Long bitrateKBPS;
    private final Long streamProfileId;
    private final int isoCountryCodeOrdinal;
   private CacheDeploymentIntentTypeAPI typeAPI;

    public CacheDeploymentIntentDelegateCachedImpl(CacheDeploymentIntentTypeAPI typeAPI, int ordinal) {
        this.bitrateKBPS = typeAPI.getBitrateKBPSBoxed(ordinal);
        this.streamProfileId = typeAPI.getStreamProfileIdBoxed(ordinal);
        this.isoCountryCodeOrdinal = typeAPI.getIsoCountryCodeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getBitrateKBPS(int ordinal) {
        return bitrateKBPS.longValue();
    }

    public Long getBitrateKBPSBoxed(int ordinal) {
        return bitrateKBPS;
    }

    public long getStreamProfileId(int ordinal) {
        return streamProfileId.longValue();
    }

    public Long getStreamProfileIdBoxed(int ordinal) {
        return streamProfileId;
    }

    public int getIsoCountryCodeOrdinal(int ordinal) {
        return isoCountryCodeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CacheDeploymentIntentTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CacheDeploymentIntentTypeAPI) typeAPI;
    }

}