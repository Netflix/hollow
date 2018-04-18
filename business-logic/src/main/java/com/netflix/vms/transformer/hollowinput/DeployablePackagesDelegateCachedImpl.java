package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DeployablePackagesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DeployablePackagesDelegate {

    private final Long packageId;
    private final Long movieId;
    private final int countryCodesOrdinal;
    private final int tagsOrdinal;
    private final Boolean defaultPackage;
    private DeployablePackagesTypeAPI typeAPI;

    public DeployablePackagesDelegateCachedImpl(DeployablePackagesTypeAPI typeAPI, int ordinal) {
        this.packageId = typeAPI.getPackageIdBoxed(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.countryCodesOrdinal = typeAPI.getCountryCodesOrdinal(ordinal);
        this.tagsOrdinal = typeAPI.getTagsOrdinal(ordinal);
        this.defaultPackage = typeAPI.getDefaultPackageBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getPackageId(int ordinal) {
        if(packageId == null)
            return Long.MIN_VALUE;
        return packageId.longValue();
    }

    public Long getPackageIdBoxed(int ordinal) {
        return packageId;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return countryCodesOrdinal;
    }

    public int getTagsOrdinal(int ordinal) {
        return tagsOrdinal;
    }

    public boolean getDefaultPackage(int ordinal) {
        if(defaultPackage == null)
            return false;
        return defaultPackage.booleanValue();
    }

    public Boolean getDefaultPackageBoxed(int ordinal) {
        return defaultPackage;
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