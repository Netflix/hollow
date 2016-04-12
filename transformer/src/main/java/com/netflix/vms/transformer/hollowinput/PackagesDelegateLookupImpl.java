package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class PackagesDelegateLookupImpl extends HollowObjectAbstractDelegate implements PackagesDelegate {

    private final PackagesTypeAPI typeAPI;

    public PackagesDelegateLookupImpl(PackagesTypeAPI typeAPI) {
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

    public int getMomentsOrdinal(int ordinal) {
        return typeAPI.getMomentsOrdinal(ordinal);
    }

    public int getDrmInfoOrdinal(int ordinal) {
        return typeAPI.getDrmInfoOrdinal(ordinal);
    }

    public int getDownloadablesOrdinal(int ordinal) {
        return typeAPI.getDownloadablesOrdinal(ordinal);
    }

    public int getDefaultS3PathComponentOrdinal(int ordinal) {
        return typeAPI.getDefaultS3PathComponentOrdinal(ordinal);
    }

    public PackagesTypeAPI getTypeAPI() {
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