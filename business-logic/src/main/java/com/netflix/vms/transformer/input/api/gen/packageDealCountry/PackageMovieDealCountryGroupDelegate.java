package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface PackageMovieDealCountryGroupDelegate extends HollowObjectDelegate {

    public long getPackageId(int ordinal);

    public Long getPackageIdBoxed(int ordinal);

    public int getPackageIdOrdinal(int ordinal);

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public String getPackageType(int ordinal);

    public boolean isPackageTypeEqual(int ordinal, String testValue);

    public int getPackageTypeOrdinal(int ordinal);

    public String getPackageStatus(int ordinal);

    public boolean isPackageStatusEqual(int ordinal, String testValue);

    public int getPackageStatusOrdinal(int ordinal);

    public int getDealCountryGroupsOrdinal(int ordinal);

    public int getTagsOrdinal(int ordinal);

    public boolean getDefaultPackage(int ordinal);

    public Boolean getDefaultPackageBoxed(int ordinal);

    public PackageMovieDealCountryGroupTypeAPI getTypeAPI();

}