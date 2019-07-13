package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PackageMovieDealCountryGroupDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PackageMovieDealCountryGroupDelegate {

    private final Long packageId;
    private final int packageIdOrdinal;
    private final Long movieId;
    private final int movieIdOrdinal;
    private final String packageType;
    private final int packageTypeOrdinal;
    private final String packageStatus;
    private final int packageStatusOrdinal;
    private final int dealCountryGroupsOrdinal;
    private final int tagsOrdinal;
    private final Boolean defaultPackage;
    private PackageMovieDealCountryGroupTypeAPI typeAPI;

    public PackageMovieDealCountryGroupDelegateCachedImpl(PackageMovieDealCountryGroupTypeAPI typeAPI, int ordinal) {
        this.packageIdOrdinal = typeAPI.getPackageIdOrdinal(ordinal);
        int packageIdTempOrdinal = packageIdOrdinal;
        this.packageId = packageIdTempOrdinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValue(packageIdTempOrdinal);
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValue(movieIdTempOrdinal);
        this.packageTypeOrdinal = typeAPI.getPackageTypeOrdinal(ordinal);
        int packageTypeTempOrdinal = packageTypeOrdinal;
        this.packageType = packageTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(packageTypeTempOrdinal);
        this.packageStatusOrdinal = typeAPI.getPackageStatusOrdinal(ordinal);
        int packageStatusTempOrdinal = packageStatusOrdinal;
        this.packageStatus = packageStatusTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(packageStatusTempOrdinal);
        this.dealCountryGroupsOrdinal = typeAPI.getDealCountryGroupsOrdinal(ordinal);
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

    public int getPackageIdOrdinal(int ordinal) {
        return packageIdOrdinal;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getMovieIdOrdinal(int ordinal) {
        return movieIdOrdinal;
    }

    public String getPackageType(int ordinal) {
        return packageType;
    }

    public boolean isPackageTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return packageType == null;
        return testValue.equals(packageType);
    }

    public int getPackageTypeOrdinal(int ordinal) {
        return packageTypeOrdinal;
    }

    public String getPackageStatus(int ordinal) {
        return packageStatus;
    }

    public boolean isPackageStatusEqual(int ordinal, String testValue) {
        if(testValue == null)
            return packageStatus == null;
        return testValue.equals(packageStatus);
    }

    public int getPackageStatusOrdinal(int ordinal) {
        return packageStatusOrdinal;
    }

    public int getDealCountryGroupsOrdinal(int ordinal) {
        return dealCountryGroupsOrdinal;
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

    public PackageMovieDealCountryGroupTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PackageMovieDealCountryGroupTypeAPI) typeAPI;
    }

}