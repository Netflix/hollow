package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieTitleNLSDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieTitleNLSDelegate {

    private final MovieTitleNLSTypeAPI typeAPI;

    public MovieTitleNLSDelegateLookupImpl(MovieTitleNLSTypeAPI typeAPI) {
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

    public String getType(int ordinal) {
        ordinal = typeAPI.getTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieTitleTypeTypeAPI().get_name(ordinal);
    }

    public boolean isTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getMovieTitleTypeTypeAPI().is_nameEqual(ordinal, testValue);
    }

    public int getTypeOrdinal(int ordinal) {
        return typeAPI.getTypeOrdinal(ordinal);
    }

    public String getTitleText(int ordinal) {
        ordinal = typeAPI.getTitleTextOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(ordinal);
    }

    public boolean isTitleTextEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getTitleTextOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getMovieTitleStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getTitleTextOrdinal(int ordinal) {
        return typeAPI.getTitleTextOrdinal(ordinal);
    }

    public String getMerchBcpCode(int ordinal) {
        ordinal = typeAPI.getMerchBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(ordinal);
    }

    public boolean isMerchBcpCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getMerchBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getBcpCodeTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getMerchBcpCodeOrdinal(int ordinal) {
        return typeAPI.getMerchBcpCodeOrdinal(ordinal);
    }

    public String getTitleBcpCode(int ordinal) {
        ordinal = typeAPI.getTitleBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(ordinal);
    }

    public boolean isTitleBcpCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getTitleBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getBcpCodeTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getTitleBcpCodeOrdinal(int ordinal) {
        return typeAPI.getTitleBcpCodeOrdinal(ordinal);
    }

    public String getSourceType(int ordinal) {
        ordinal = typeAPI.getSourceTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getTitleSourceTypeTypeAPI().get_name(ordinal);
    }

    public boolean isSourceTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getSourceTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getTitleSourceTypeTypeAPI().is_nameEqual(ordinal, testValue);
    }

    public int getSourceTypeOrdinal(int ordinal) {
        return typeAPI.getSourceTypeOrdinal(ordinal);
    }

    public boolean getIsOriginalTitle(int ordinal) {
        ordinal = typeAPI.getIsOriginalTitleOrdinal(ordinal);
        return ordinal == -1 ? false : typeAPI.getAPI().getIsOriginalTitleTypeAPI().getValue(ordinal);
    }

    public Boolean getIsOriginalTitleBoxed(int ordinal) {
        ordinal = typeAPI.getIsOriginalTitleOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getIsOriginalTitleTypeAPI().getValueBoxed(ordinal);
    }

    public int getIsOriginalTitleOrdinal(int ordinal) {
        return typeAPI.getIsOriginalTitleOrdinal(ordinal);
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

    public MovieTitleNLSTypeAPI getTypeAPI() {
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