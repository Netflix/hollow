package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoRightsRightsContractsPackagesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRightsRightsContractsPackagesDelegate {

    private final Long packageId;
    private final Boolean primary;
   private VideoRightsRightsContractsPackagesTypeAPI typeAPI;

    public VideoRightsRightsContractsPackagesDelegateCachedImpl(VideoRightsRightsContractsPackagesTypeAPI typeAPI, int ordinal) {
        this.packageId = typeAPI.getPackageIdBoxed(ordinal);
        this.primary = typeAPI.getPrimaryBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getPackageId(int ordinal) {
        return packageId.longValue();
    }

    public Long getPackageIdBoxed(int ordinal) {
        return packageId;
    }

    public boolean getPrimary(int ordinal) {
        return primary.booleanValue();
    }

    public Boolean getPrimaryBoxed(int ordinal) {
        return primary;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoRightsRightsContractsPackagesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRightsRightsContractsPackagesTypeAPI) typeAPI;
    }

}