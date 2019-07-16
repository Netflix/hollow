package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MovieReleaseHistoryTypeAPI extends HollowObjectTypeAPI {

    private final MovieReleaseHistoryDelegateLookupImpl delegateLookupImpl;

    public MovieReleaseHistoryTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "countryCode",
            "type",
            "year",
            "month",
            "day",
            "distributorName",
            "distributorBcpCode",
            "dateCreated",
            "lastUpdated",
            "createdBy",
            "updatedBy"
        });
        this.delegateLookupImpl = new MovieReleaseHistoryDelegateLookupImpl(this);
    }

    public int getMovieIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieReleaseHistory", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MovieIdTypeAPI getMovieIdTypeAPI() {
        return getAPI().getMovieIdTypeAPI();
    }

    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieReleaseHistory", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public CountryStringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getCountryStringTypeAPI();
    }

    public int getTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieReleaseHistory", ordinal, "type");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public MovieReleaseTypeTypeAPI getTypeTypeAPI() {
        return getAPI().getMovieReleaseTypeTypeAPI();
    }

    public int getYear(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleInt("MovieReleaseHistory", ordinal, "year");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
    }

    public Integer getYearBoxed(int ordinal) {
        int i;
        if(fieldIndex[3] == -1) {
            i = missingDataHandler().handleInt("MovieReleaseHistory", ordinal, "year");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getMonth(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleInt("MovieReleaseHistory", ordinal, "month");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[4]);
    }

    public Integer getMonthBoxed(int ordinal) {
        int i;
        if(fieldIndex[4] == -1) {
            i = missingDataHandler().handleInt("MovieReleaseHistory", ordinal, "month");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[4]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getDay(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleInt("MovieReleaseHistory", ordinal, "day");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[5]);
    }

    public Integer getDayBoxed(int ordinal) {
        int i;
        if(fieldIndex[5] == -1) {
            i = missingDataHandler().handleInt("MovieReleaseHistory", ordinal, "day");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[5]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[5]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getDistributorNameOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieReleaseHistory", ordinal, "distributorName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public DistributorNameTypeAPI getDistributorNameTypeAPI() {
        return getAPI().getDistributorNameTypeAPI();
    }

    public int getDistributorBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieReleaseHistory", ordinal, "distributorBcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public BcpCodeTypeAPI getDistributorBcpCodeTypeAPI() {
        return getAPI().getBcpCodeTypeAPI();
    }

    public int getDateCreatedOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieReleaseHistory", ordinal, "dateCreated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public DateTypeAPI getDateCreatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieReleaseHistory", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getCreatedByOrdinal(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieReleaseHistory", ordinal, "createdBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[10]);
    }

    public StringTypeAPI getCreatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByOrdinal(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieReleaseHistory", ordinal, "updatedBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[11]);
    }

    public StringTypeAPI getUpdatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public MovieReleaseHistoryDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}