package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TitleSetupRequirementsDelegateLookupImpl extends HollowObjectAbstractDelegate implements TitleSetupRequirementsDelegate {

    private final TitleSetupRequirementsTypeAPI typeAPI;

    public TitleSetupRequirementsDelegateLookupImpl(TitleSetupRequirementsTypeAPI typeAPI) {
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

    public int getTitleSetupRequirementsTemplateOrdinal(int ordinal) {
        return typeAPI.getTitleSetupRequirementsTemplateOrdinal(ordinal);
    }

    public boolean getSubsRequired(int ordinal) {
        return typeAPI.getSubsRequired(ordinal);
    }

    public Boolean getSubsRequiredBoxed(int ordinal) {
        return typeAPI.getSubsRequiredBoxed(ordinal);
    }

    public boolean getDubsRequired(int ordinal) {
        return typeAPI.getDubsRequired(ordinal);
    }

    public Boolean getDubsRequiredBoxed(int ordinal) {
        return typeAPI.getDubsRequiredBoxed(ordinal);
    }

    public boolean getArtworkRequired(int ordinal) {
        return typeAPI.getArtworkRequired(ordinal);
    }

    public Boolean getArtworkRequiredBoxed(int ordinal) {
        return typeAPI.getArtworkRequiredBoxed(ordinal);
    }

    public boolean getInstreamStillsRequired(int ordinal) {
        return typeAPI.getInstreamStillsRequired(ordinal);
    }

    public Boolean getInstreamStillsRequiredBoxed(int ordinal) {
        return typeAPI.getInstreamStillsRequiredBoxed(ordinal);
    }

    public boolean getInformativeSynopsisRequired(int ordinal) {
        return typeAPI.getInformativeSynopsisRequired(ordinal);
    }

    public Boolean getInformativeSynopsisRequiredBoxed(int ordinal) {
        return typeAPI.getInformativeSynopsisRequiredBoxed(ordinal);
    }

    public String getRatingsRequired(int ordinal) {
        ordinal = typeAPI.getRatingsRequiredOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getRatingsRequirementsTypeAPI().get_name(ordinal);
    }

    public boolean isRatingsRequiredEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getRatingsRequiredOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getRatingsRequirementsTypeAPI().is_nameEqual(ordinal, testValue);
    }

    public int getRatingsRequiredOrdinal(int ordinal) {
        return typeAPI.getRatingsRequiredOrdinal(ordinal);
    }

    public boolean getTaggingRequired(int ordinal) {
        return typeAPI.getTaggingRequired(ordinal);
    }

    public Boolean getTaggingRequiredBoxed(int ordinal) {
        return typeAPI.getTaggingRequiredBoxed(ordinal);
    }

    public boolean getCastRequired(int ordinal) {
        return typeAPI.getCastRequired(ordinal);
    }

    public Boolean getCastRequiredBoxed(int ordinal) {
        return typeAPI.getCastRequiredBoxed(ordinal);
    }

    public boolean getDisplayNameRequired(int ordinal) {
        return typeAPI.getDisplayNameRequired(ordinal);
    }

    public Boolean getDisplayNameRequiredBoxed(int ordinal) {
        return typeAPI.getDisplayNameRequiredBoxed(ordinal);
    }

    public String getSourceRequestDefaultFulfillment(int ordinal) {
        ordinal = typeAPI.getSourceRequestDefaultFulfillmentOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getSourceRequestDefaultFulfillmentTypeAPI().get_name(ordinal);
    }

    public boolean isSourceRequestDefaultFulfillmentEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getSourceRequestDefaultFulfillmentOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getSourceRequestDefaultFulfillmentTypeAPI().is_nameEqual(ordinal, testValue);
    }

    public int getSourceRequestDefaultFulfillmentOrdinal(int ordinal) {
        return typeAPI.getSourceRequestDefaultFulfillmentOrdinal(ordinal);
    }

    public String getRecipeGroups(int ordinal) {
        ordinal = typeAPI.getRecipeGroupsOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getRecipeGroupsTypeAPI().getValue(ordinal);
    }

    public boolean isRecipeGroupsEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getRecipeGroupsOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getRecipeGroupsTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getRecipeGroupsOrdinal(int ordinal) {
        return typeAPI.getRecipeGroupsOrdinal(ordinal);
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

    public TitleSetupRequirementsTypeAPI getTypeAPI() {
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