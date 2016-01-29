package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class DeployablePackagesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DeployablePackagesDelegate {

    private final int countryCodesOrdinal;
    private final Long packageId;
    private final Long movieId;
   private DeployablePackagesTypeAPI typeAPI;

    public DeployablePackagesDelegateCachedImpl(DeployablePackagesTypeAPI typeAPI, int ordinal) {
        this.countryCodesOrdinal = typeAPI.getCountryCodesOrdinal(ordinal);
        this.packageId = typeAPI.getPackageIdBoxed(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return countryCodesOrdinal;
    }

    public long getPackageId(int ordinal) {
        return packageId.longValue();
    }

    public Long getPackageIdBoxed(int ordinal) {
        return packageId;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public DeployablePackagesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (DeployablePackagesTypeAPI) typeAPI;
    }

}