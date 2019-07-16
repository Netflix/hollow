package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieReleaseHistoryDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieReleaseHistoryDelegate {

    private final MovieReleaseHistoryTypeAPI typeAPI;

    public MovieReleaseHistoryDelegateLookupImpl(MovieReleaseHistoryTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        ordinal = typeAPI.getMovieIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getMovieIdTypeAPI().getValue(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        ordinal = typeAPI.getMovieIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValueBoxed(ordinal);
    }

    public int getMovieIdOrdinal(int ordinal) {
        return typeAPI.getMovieIdOrdinal(ordinal);
    }

    public String getCountryCode(int ordinal) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getCountryStringTypeAPI().getValue(ordinal);
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getCountryStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public String getType(int ordinal) {
        ordinal = typeAPI.getTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieReleaseTypeTypeAPI().get_name(ordinal);
    }

    public boolean isTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getMovieReleaseTypeTypeAPI().is_nameEqual(ordinal, testValue);
    }

    public int getTypeOrdinal(int ordinal) {
        return typeAPI.getTypeOrdinal(ordinal);
    }

    public int getYear(int ordinal) {
        return typeAPI.getYear(ordinal);
    }

    public Integer getYearBoxed(int ordinal) {
        return typeAPI.getYearBoxed(ordinal);
    }

    public int getMonth(int ordinal) {
        return typeAPI.getMonth(ordinal);
    }

    public Integer getMonthBoxed(int ordinal) {
        return typeAPI.getMonthBoxed(ordinal);
    }

    public int getDay(int ordinal) {
        return typeAPI.getDay(ordinal);
    }

    public Integer getDayBoxed(int ordinal) {
        return typeAPI.getDayBoxed(ordinal);
    }

    public String getDistributorName(int ordinal) {
        ordinal = typeAPI.getDistributorNameOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getDistributorNameTypeAPI().getValue(ordinal);
    }

    public boolean isDistributorNameEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getDistributorNameOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getDistributorNameTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getDistributorNameOrdinal(int ordinal) {
        return typeAPI.getDistributorNameOrdinal(ordinal);
    }

    public String getDistributorBcpCode(int ordinal) {
        ordinal = typeAPI.getDistributorBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(ordinal);
    }

    public boolean isDistributorBcpCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getDistributorBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getBcpCodeTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getDistributorBcpCodeOrdinal(int ordinal) {
        return typeAPI.getDistributorBcpCodeOrdinal(ordinal);
    }

    public long getDateCreated(int ordinal) {
        ordinal = typeAPI.getDateCreatedOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getDateTypeAPI().getValue(ordinal);
    }

    public Long getDateCreatedBoxed(int ordinal) {
        ordinal = typeAPI.getDateCreatedOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValueBoxed(ordinal);
    }

    public int getDateCreatedOrdinal(int ordinal) {
        return typeAPI.getDateCreatedOrdinal(ordinal);
    }

    public long getLastUpdated(int ordinal) {
        ordinal = typeAPI.getLastUpdatedOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getDateTypeAPI().getValue(ordinal);
    }

    public Long getLastUpdatedBoxed(int ordinal) {
        ordinal = typeAPI.getLastUpdatedOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValueBoxed(ordinal);
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        return typeAPI.getLastUpdatedOrdinal(ordinal);
    }

    public String getCreatedBy(int ordinal) {
        ordinal = typeAPI.getCreatedByOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCreatedByEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCreatedByOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCreatedByOrdinal(int ordinal) {
        return typeAPI.getCreatedByOrdinal(ordinal);
    }

    public String getUpdatedBy(int ordinal) {
        ordinal = typeAPI.getUpdatedByOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isUpdatedByEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getUpdatedByOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getUpdatedByOrdinal(int ordinal) {
        return typeAPI.getUpdatedByOrdinal(ordinal);
    }

    public MovieReleaseHistoryTypeAPI getTypeAPI() {
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