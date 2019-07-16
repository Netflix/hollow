package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MovieReleaseHistoryDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieReleaseHistoryDelegate {

    private final Long movieId;
    private final int movieIdOrdinal;
    private final String countryCode;
    private final int countryCodeOrdinal;
    private final String type;
    private final int typeOrdinal;
    private final Integer year;
    private final Integer month;
    private final Integer day;
    private final String distributorName;
    private final int distributorNameOrdinal;
    private final String distributorBcpCode;
    private final int distributorBcpCodeOrdinal;
    private final Long dateCreated;
    private final int dateCreatedOrdinal;
    private final Long lastUpdated;
    private final int lastUpdatedOrdinal;
    private final String createdBy;
    private final int createdByOrdinal;
    private final String updatedBy;
    private final int updatedByOrdinal;
    private MovieReleaseHistoryTypeAPI typeAPI;

    public MovieReleaseHistoryDelegateCachedImpl(MovieReleaseHistoryTypeAPI typeAPI, int ordinal) {
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValue(movieIdTempOrdinal);
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        int countryCodeTempOrdinal = countryCodeOrdinal;
        this.countryCode = countryCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getCountryStringTypeAPI().getValue(countryCodeTempOrdinal);
        this.typeOrdinal = typeAPI.getTypeOrdinal(ordinal);
        int typeTempOrdinal = typeOrdinal;
        this.type = typeTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieReleaseTypeTypeAPI().get_name(typeTempOrdinal);
        this.year = typeAPI.getYearBoxed(ordinal);
        this.month = typeAPI.getMonthBoxed(ordinal);
        this.day = typeAPI.getDayBoxed(ordinal);
        this.distributorNameOrdinal = typeAPI.getDistributorNameOrdinal(ordinal);
        int distributorNameTempOrdinal = distributorNameOrdinal;
        this.distributorName = distributorNameTempOrdinal == -1 ? null : typeAPI.getAPI().getDistributorNameTypeAPI().getValue(distributorNameTempOrdinal);
        this.distributorBcpCodeOrdinal = typeAPI.getDistributorBcpCodeOrdinal(ordinal);
        int distributorBcpCodeTempOrdinal = distributorBcpCodeOrdinal;
        this.distributorBcpCode = distributorBcpCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(distributorBcpCodeTempOrdinal);
        this.dateCreatedOrdinal = typeAPI.getDateCreatedOrdinal(ordinal);
        int dateCreatedTempOrdinal = dateCreatedOrdinal;
        this.dateCreated = dateCreatedTempOrdinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValue(dateCreatedTempOrdinal);
        this.lastUpdatedOrdinal = typeAPI.getLastUpdatedOrdinal(ordinal);
        int lastUpdatedTempOrdinal = lastUpdatedOrdinal;
        this.lastUpdated = lastUpdatedTempOrdinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValue(lastUpdatedTempOrdinal);
        this.createdByOrdinal = typeAPI.getCreatedByOrdinal(ordinal);
        int createdByTempOrdinal = createdByOrdinal;
        this.createdBy = createdByTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(createdByTempOrdinal);
        this.updatedByOrdinal = typeAPI.getUpdatedByOrdinal(ordinal);
        int updatedByTempOrdinal = updatedByOrdinal;
        this.updatedBy = updatedByTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(updatedByTempOrdinal);
        this.typeAPI = typeAPI;
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

    public String getCountryCode(int ordinal) {
        return countryCode;
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return countryCode == null;
        return testValue.equals(countryCode);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public String getType(int ordinal) {
        return type;
    }

    public boolean isTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return type == null;
        return testValue.equals(type);
    }

    public int getTypeOrdinal(int ordinal) {
        return typeOrdinal;
    }

    public int getYear(int ordinal) {
        if(year == null)
            return Integer.MIN_VALUE;
        return year.intValue();
    }

    public Integer getYearBoxed(int ordinal) {
        return year;
    }

    public int getMonth(int ordinal) {
        if(month == null)
            return Integer.MIN_VALUE;
        return month.intValue();
    }

    public Integer getMonthBoxed(int ordinal) {
        return month;
    }

    public int getDay(int ordinal) {
        if(day == null)
            return Integer.MIN_VALUE;
        return day.intValue();
    }

    public Integer getDayBoxed(int ordinal) {
        return day;
    }

    public String getDistributorName(int ordinal) {
        return distributorName;
    }

    public boolean isDistributorNameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return distributorName == null;
        return testValue.equals(distributorName);
    }

    public int getDistributorNameOrdinal(int ordinal) {
        return distributorNameOrdinal;
    }

    public String getDistributorBcpCode(int ordinal) {
        return distributorBcpCode;
    }

    public boolean isDistributorBcpCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return distributorBcpCode == null;
        return testValue.equals(distributorBcpCode);
    }

    public int getDistributorBcpCodeOrdinal(int ordinal) {
        return distributorBcpCodeOrdinal;
    }

    public long getDateCreated(int ordinal) {
        if(dateCreated == null)
            return Long.MIN_VALUE;
        return dateCreated.longValue();
    }

    public Long getDateCreatedBoxed(int ordinal) {
        return dateCreated;
    }

    public int getDateCreatedOrdinal(int ordinal) {
        return dateCreatedOrdinal;
    }

    public long getLastUpdated(int ordinal) {
        if(lastUpdated == null)
            return Long.MIN_VALUE;
        return lastUpdated.longValue();
    }

    public Long getLastUpdatedBoxed(int ordinal) {
        return lastUpdated;
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        return lastUpdatedOrdinal;
    }

    public String getCreatedBy(int ordinal) {
        return createdBy;
    }

    public boolean isCreatedByEqual(int ordinal, String testValue) {
        if(testValue == null)
            return createdBy == null;
        return testValue.equals(createdBy);
    }

    public int getCreatedByOrdinal(int ordinal) {
        return createdByOrdinal;
    }

    public String getUpdatedBy(int ordinal) {
        return updatedBy;
    }

    public boolean isUpdatedByEqual(int ordinal, String testValue) {
        if(testValue == null)
            return updatedBy == null;
        return testValue.equals(updatedBy);
    }

    public int getUpdatedByOrdinal(int ordinal) {
        return updatedByOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public MovieReleaseHistoryTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieReleaseHistoryTypeAPI) typeAPI;
    }

}