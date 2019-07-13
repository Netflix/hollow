package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PackageMovieDealCountryGroupTypeAPI extends HollowObjectTypeAPI {

    private final PackageMovieDealCountryGroupDelegateLookupImpl delegateLookupImpl;

    public PackageMovieDealCountryGroupTypeAPI(PackageDealCountryAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "packageId",
            "movieId",
            "packageType",
            "packageStatus",
            "dealCountryGroups",
            "tags",
            "defaultPackage"
        });
        this.delegateLookupImpl = new PackageMovieDealCountryGroupDelegateLookupImpl(this);
    }

    public int getPackageIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageMovieDealCountryGroup", ordinal, "packageId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public LongTypeAPI getPackageIdTypeAPI() {
        return getAPI().getLongTypeAPI();
    }

    public int getMovieIdOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageMovieDealCountryGroup", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public LongTypeAPI getMovieIdTypeAPI() {
        return getAPI().getLongTypeAPI();
    }

    public int getPackageTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageMovieDealCountryGroup", ordinal, "packageType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getPackageTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getPackageStatusOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageMovieDealCountryGroup", ordinal, "packageStatus");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getPackageStatusTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getDealCountryGroupsOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageMovieDealCountryGroup", ordinal, "dealCountryGroups");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public ListOfDealCountryGroupTypeAPI getDealCountryGroupsTypeAPI() {
        return getAPI().getListOfDealCountryGroupTypeAPI();
    }

    public int getTagsOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageMovieDealCountryGroup", ordinal, "tags");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public ListOfPackageTagsTypeAPI getTagsTypeAPI() {
        return getAPI().getListOfPackageTagsTypeAPI();
    }

    public boolean getDefaultPackage(int ordinal) {
        if(fieldIndex[6] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("PackageMovieDealCountryGroup", ordinal, "defaultPackage"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]));
    }

    public Boolean getDefaultPackageBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("PackageMovieDealCountryGroup", ordinal, "defaultPackage");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public PackageMovieDealCountryGroupDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public PackageDealCountryAPI getAPI() {
        return (PackageDealCountryAPI) api;
    }

}