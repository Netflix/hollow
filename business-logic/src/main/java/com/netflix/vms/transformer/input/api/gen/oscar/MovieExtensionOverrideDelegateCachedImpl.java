package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MovieExtensionOverrideDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieExtensionOverrideDelegate {

    private final String entityType;
    private final int entityTypeOrdinal;
    private final String entityValue;
    private final int entityValueOrdinal;
    private final String attributeValue;
    private final int attributeValueOrdinal;
    private final Long dateCreated;
    private final int dateCreatedOrdinal;
    private final Long lastUpdated;
    private final int lastUpdatedOrdinal;
    private final String createdBy;
    private final int createdByOrdinal;
    private final String updatedBy;
    private final int updatedByOrdinal;
    private MovieExtensionOverrideTypeAPI typeAPI;

    public MovieExtensionOverrideDelegateCachedImpl(MovieExtensionOverrideTypeAPI typeAPI, int ordinal) {
        this.entityTypeOrdinal = typeAPI.getEntityTypeOrdinal(ordinal);
        int entityTypeTempOrdinal = entityTypeOrdinal;
        this.entityType = entityTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getOverrideEntityTypeTypeAPI().get_name(entityTypeTempOrdinal);
        this.entityValueOrdinal = typeAPI.getEntityValueOrdinal(ordinal);
        int entityValueTempOrdinal = entityValueOrdinal;
        this.entityValue = entityValueTempOrdinal == -1 ? null : typeAPI.getAPI().getOverrideEntityValueTypeAPI().getValue(entityValueTempOrdinal);
        this.attributeValueOrdinal = typeAPI.getAttributeValueOrdinal(ordinal);
        int attributeValueTempOrdinal = attributeValueOrdinal;
        this.attributeValue = attributeValueTempOrdinal == -1 ? null : typeAPI.getAPI().getAttributeValueTypeAPI().getValue(attributeValueTempOrdinal);
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

    public String getEntityType(int ordinal) {
        return entityType;
    }

    public boolean isEntityTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return entityType == null;
        return testValue.equals(entityType);
    }

    public int getEntityTypeOrdinal(int ordinal) {
        return entityTypeOrdinal;
    }

    public String getEntityValue(int ordinal) {
        return entityValue;
    }

    public boolean isEntityValueEqual(int ordinal, String testValue) {
        if(testValue == null)
            return entityValue == null;
        return testValue.equals(entityValue);
    }

    public int getEntityValueOrdinal(int ordinal) {
        return entityValueOrdinal;
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

    public MovieExtensionOverrideTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieExtensionOverrideTypeAPI) typeAPI;
    }

}