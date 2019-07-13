package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PackageMovieDealCountryGroupDelegateLookupImpl extends HollowObjectAbstractDelegate implements PackageMovieDealCountryGroupDelegate {

    private final PackageMovieDealCountryGroupTypeAPI typeAPI;

    public PackageMovieDealCountryGroupDelegateLookupImpl(PackageMovieDealCountryGroupTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getPackageId(int ordinal) {
        ordinal = typeAPI.getPackageIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getLongTypeAPI().getValue(ordinal);
    }

    public Long getPackageIdBoxed(int ordinal) {
        ordinal = typeAPI.getPackageIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValueBoxed(ordinal);
    }

    public int getPackageIdOrdinal(int ordinal) {
        return typeAPI.getPackageIdOrdinal(ordinal);
    }

    public long getMovieId(int ordinal) {
        ordinal = typeAPI.getMovieIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getLongTypeAPI().getValue(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        ordinal = typeAPI.getMovieIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValueBoxed(ordinal);
    }

    public int getMovieIdOrdinal(int ordinal) {
        return typeAPI.getMovieIdOrdinal(ordinal);
    }

    public String getPackageType(int ordinal) {
        ordinal = typeAPI.getPackageTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isPackageTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getPackageTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getPackageTypeOrdinal(int ordinal) {
        return typeAPI.getPackageTypeOrdinal(ordinal);
    }

    public String getPackageStatus(int ordinal) {
        ordinal = typeAPI.getPackageStatusOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isPackageStatusEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getPackageStatusOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getPackageStatusOrdinal(int ordinal) {
        return typeAPI.getPackageStatusOrdinal(ordinal);
    }

    public int getDealCountryGroupsOrdinal(int ordinal) {
        return typeAPI.getDealCountryGroupsOrdinal(ordinal);
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

    public PackageMovieDealCountryGroupTypeAPI getTypeAPI() {
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