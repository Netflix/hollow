package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DeployablePackagesDelegateLookupImpl extends HollowObjectAbstractDelegate implements DeployablePackagesDelegate {

    private final DeployablePackagesTypeAPI typeAPI;

    public DeployablePackagesDelegateLookupImpl(DeployablePackagesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getPackageId(int ordinal) {
        return typeAPI.getPackageId(ordinal);
    }

    public Long getPackageIdBoxed(int ordinal) {
        return typeAPI.getPackageIdBoxed(ordinal);
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return typeAPI.getCountryCodesOrdinal(ordinal);
    }

    public int getTagsOrdinal(int ordinal) {
        return typeAPI.getTagsOrdinal(ordinal);
    }

    public boolean getDefaultPackage(int ordinal) {
        return typeAPI.getDefaultPackage(ordinal);
    }

    public Boolean getDefaultPackageBoxed(int ordinal) {
        return typeAPI.getDefaultPackageBoxed(ordinal);
    }

    public DeployablePackagesTypeAPI getTypeAPI() {
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