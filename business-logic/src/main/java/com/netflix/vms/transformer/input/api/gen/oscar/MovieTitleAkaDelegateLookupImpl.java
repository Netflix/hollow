package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieTitleAkaDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieTitleAkaDelegate {

    private final MovieTitleAkaTypeAPI typeAPI;

    public MovieTitleAkaDelegateLookupImpl(MovieTitleAkaTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return typeAPI.getId(ordinal);
    }

    public Long getIdBoxed(int ordinal) {
        return typeAPI.getIdBoxed(ordinal);
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

    public String getAlias(int ordinal) {
        ordinal = typeAPI.getAliasOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(ordinal);
    }

    public boolean isAliasEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getAliasOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getMovieTitleStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getAliasOrdinal(int ordinal) {
        return typeAPI.getAliasOrdinal(ordinal);
    }

    public String getBcpCode(int ordinal) {
        ordinal = typeAPI.getBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(ordinal);
    }

    public boolean isBcpCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getBcpCodeTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getBcpCodeOrdinal(int ordinal) {
        return typeAPI.getBcpCodeOrdinal(ordinal);
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

    public MovieTitleAkaTypeAPI getTypeAPI() {
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