package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieExtensionDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieExtensionDelegate {

    private final MovieExtensionTypeAPI typeAPI;

    public MovieExtensionDelegateLookupImpl(MovieExtensionTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieExtensionId(int ordinal) {
        return typeAPI.getMovieExtensionId(ordinal);
    }

    public Long getMovieExtensionIdBoxed(int ordinal) {
        return typeAPI.getMovieExtensionIdBoxed(ordinal);
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

    public String getAttributeName(int ordinal) {
        ordinal = typeAPI.getAttributeNameOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getAttributeNameTypeAPI().getValue(ordinal);
    }

    public boolean isAttributeNameEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getAttributeNameOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getAttributeNameTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getAttributeNameOrdinal(int ordinal) {
        return typeAPI.getAttributeNameOrdinal(ordinal);
    }

    public String getAttributeValue(int ordinal) {
        ordinal = typeAPI.getAttributeValueOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getAttributeValueTypeAPI().getValue(ordinal);
    }

    public boolean isAttributeValueEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getAttributeValueOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getAttributeValueTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getAttributeValueOrdinal(int ordinal) {
        return typeAPI.getAttributeValueOrdinal(ordinal);
    }

    public int getOverridesOrdinal(int ordinal) {
        return typeAPI.getOverridesOrdinal(ordinal);
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

    public MovieExtensionTypeAPI getTypeAPI() {
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