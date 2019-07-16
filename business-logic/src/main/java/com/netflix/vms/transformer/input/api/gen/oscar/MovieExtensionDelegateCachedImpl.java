package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MovieExtensionDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieExtensionDelegate {

    private final Long movieExtensionId;
    private final Long movieId;
    private final int movieIdOrdinal;
    private final String attributeName;
    private final int attributeNameOrdinal;
    private final String attributeValue;
    private final int attributeValueOrdinal;
    private final int overridesOrdinal;
    private final Long dateCreated;
    private final int dateCreatedOrdinal;
    private final Long lastUpdated;
    private final int lastUpdatedOrdinal;
    private final String createdBy;
    private final int createdByOrdinal;
    private final String updatedBy;
    private final int updatedByOrdinal;
    private MovieExtensionTypeAPI typeAPI;

    public MovieExtensionDelegateCachedImpl(MovieExtensionTypeAPI typeAPI, int ordinal) {
        this.movieExtensionId = typeAPI.getMovieExtensionIdBoxed(ordinal);
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValue(movieIdTempOrdinal);
        this.attributeNameOrdinal = typeAPI.getAttributeNameOrdinal(ordinal);
        int attributeNameTempOrdinal = attributeNameOrdinal;
        this.attributeName = attributeNameTempOrdinal == -1 ? null : typeAPI.getAPI().getAttributeNameTypeAPI().getValue(attributeNameTempOrdinal);
        this.attributeValueOrdinal = typeAPI.getAttributeValueOrdinal(ordinal);
        int attributeValueTempOrdinal = attributeValueOrdinal;
        this.attributeValue = attributeValueTempOrdinal == -1 ? null : typeAPI.getAPI().getAttributeValueTypeAPI().getValue(attributeValueTempOrdinal);
        this.overridesOrdinal = typeAPI.getOverridesOrdinal(ordinal);
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

    public long getMovieExtensionId(int ordinal) {
        if(movieExtensionId == null)
            return Long.MIN_VALUE;
        return movieExtensionId.longValue();
    }

    public Long getMovieExtensionIdBoxed(int ordinal) {
        return movieExtensionId;
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

    public String getAttributeName(int ordinal) {
        return attributeName;
    }

    public boolean isAttributeNameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return attributeName == null;
        return testValue.equals(attributeName);
    }

    public int getAttributeNameOrdinal(int ordinal) {
        return attributeNameOrdinal;
    }

    public String getAttributeValue(int ordinal) {
        return attributeValue;
    }

    public boolean isAttributeValueEqual(int ordinal, String testValue) {
        if(testValue == null)
            return attributeValue == null;
        return testValue.equals(attributeValue);
    }

    public int getAttributeValueOrdinal(int ordinal) {
        return attributeValueOrdinal;
    }

    public int getOverridesOrdinal(int ordinal) {
        return overridesOrdinal;
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

    public MovieExtensionTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieExtensionTypeAPI) typeAPI;
    }

}