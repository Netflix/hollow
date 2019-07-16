package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class TitleSetupRequirementsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TitleSetupRequirementsDelegate {

    private final Long movieId;
    private final int movieIdOrdinal;
    private final int titleSetupRequirementsTemplateOrdinal;
    private final Boolean subsRequired;
    private final Boolean dubsRequired;
    private final Boolean artworkRequired;
    private final Boolean instreamStillsRequired;
    private final Boolean informativeSynopsisRequired;
    private final String ratingsRequired;
    private final int ratingsRequiredOrdinal;
    private final Boolean taggingRequired;
    private final Boolean castRequired;
    private final Boolean displayNameRequired;
    private final String sourceRequestDefaultFulfillment;
    private final int sourceRequestDefaultFulfillmentOrdinal;
    private final String recipeGroups;
    private final int recipeGroupsOrdinal;
    private final Long dateCreated;
    private final int dateCreatedOrdinal;
    private final Long lastUpdated;
    private final int lastUpdatedOrdinal;
    private final String createdBy;
    private final int createdByOrdinal;
    private final String updatedBy;
    private final int updatedByOrdinal;
    private TitleSetupRequirementsTypeAPI typeAPI;

    public TitleSetupRequirementsDelegateCachedImpl(TitleSetupRequirementsTypeAPI typeAPI, int ordinal) {
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValue(movieIdTempOrdinal);
        this.titleSetupRequirementsTemplateOrdinal = typeAPI.getTitleSetupRequirementsTemplateOrdinal(ordinal);
        this.subsRequired = typeAPI.getSubsRequiredBoxed(ordinal);
        this.dubsRequired = typeAPI.getDubsRequiredBoxed(ordinal);
        this.artworkRequired = typeAPI.getArtworkRequiredBoxed(ordinal);
        this.instreamStillsRequired = typeAPI.getInstreamStillsRequiredBoxed(ordinal);
        this.informativeSynopsisRequired = typeAPI.getInformativeSynopsisRequiredBoxed(ordinal);
        this.ratingsRequiredOrdinal = typeAPI.getRatingsRequiredOrdinal(ordinal);
        int ratingsRequiredTempOrdinal = ratingsRequiredOrdinal;
        this.ratingsRequired = ratingsRequiredTempOrdinal == -1 ? null : typeAPI.getAPI().getRatingsRequirementsTypeAPI().get_name(ratingsRequiredTempOrdinal);
        this.taggingRequired = typeAPI.getTaggingRequiredBoxed(ordinal);
        this.castRequired = typeAPI.getCastRequiredBoxed(ordinal);
        this.displayNameRequired = typeAPI.getDisplayNameRequiredBoxed(ordinal);
        this.sourceRequestDefaultFulfillmentOrdinal = typeAPI.getSourceRequestDefaultFulfillmentOrdinal(ordinal);
        int sourceRequestDefaultFulfillmentTempOrdinal = sourceRequestDefaultFulfillmentOrdinal;
        this.sourceRequestDefaultFulfillment = sourceRequestDefaultFulfillmentTempOrdinal == -1 ? null : typeAPI.getAPI().getSourceRequestDefaultFulfillmentTypeAPI().get_name(sourceRequestDefaultFulfillmentTempOrdinal);
        this.recipeGroupsOrdinal = typeAPI.getRecipeGroupsOrdinal(ordinal);
        int recipeGroupsTempOrdinal = recipeGroupsOrdinal;
        this.recipeGroups = recipeGroupsTempOrdinal == -1 ? null : typeAPI.getAPI().getRecipeGroupsTypeAPI().getValue(recipeGroupsTempOrdinal);
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

    public int getTitleSetupRequirementsTemplateOrdinal(int ordinal) {
        return titleSetupRequirementsTemplateOrdinal;
    }

    public boolean getSubsRequired(int ordinal) {
        if(subsRequired == null)
            return false;
        return subsRequired.booleanValue();
    }

    public Boolean getSubsRequiredBoxed(int ordinal) {
        return subsRequired;
    }

    public boolean getDubsRequired(int ordinal) {
        if(dubsRequired == null)
            return false;
        return dubsRequired.booleanValue();
    }

    public Boolean getDubsRequiredBoxed(int ordinal) {
        return dubsRequired;
    }

    public boolean getArtworkRequired(int ordinal) {
        if(artworkRequired == null)
            return false;
        return artworkRequired.booleanValue();
    }

    public Boolean getArtworkRequiredBoxed(int ordinal) {
        return artworkRequired;
    }

    public boolean getInstreamStillsRequired(int ordinal) {
        if(instreamStillsRequired == null)
            return false;
        return instreamStillsRequired.booleanValue();
    }

    public Boolean getInstreamStillsRequiredBoxed(int ordinal) {
        return instreamStillsRequired;
    }

    public boolean getInformativeSynopsisRequired(int ordinal) {
        if(informativeSynopsisRequired == null)
            return false;
        return informativeSynopsisRequired.booleanValue();
    }

    public Boolean getInformativeSynopsisRequiredBoxed(int ordinal) {
        return informativeSynopsisRequired;
    }

    public String getRatingsRequired(int ordinal) {
        return ratingsRequired;
    }

    public boolean isRatingsRequiredEqual(int ordinal, String testValue) {
        if(testValue == null)
            return ratingsRequired == null;
        return testValue.equals(ratingsRequired);
    }

    public int getRatingsRequiredOrdinal(int ordinal) {
        return ratingsRequiredOrdinal;
    }

    public boolean getTaggingRequired(int ordinal) {
        if(taggingRequired == null)
            return false;
        return taggingRequired.booleanValue();
    }

    public Boolean getTaggingRequiredBoxed(int ordinal) {
        return taggingRequired;
    }

    public boolean getCastRequired(int ordinal) {
        if(castRequired == null)
            return false;
        return castRequired.booleanValue();
    }

    public Boolean getCastRequiredBoxed(int ordinal) {
        return castRequired;
    }

    public boolean getDisplayNameRequired(int ordinal) {
        if(displayNameRequired == null)
            return false;
        return displayNameRequired.booleanValue();
    }

    public Boolean getDisplayNameRequiredBoxed(int ordinal) {
        return displayNameRequired;
    }

    public String getSourceRequestDefaultFulfillment(int ordinal) {
        return sourceRequestDefaultFulfillment;
    }

    public boolean isSourceRequestDefaultFulfillmentEqual(int ordinal, String testValue) {
        if(testValue == null)
            return sourceRequestDefaultFulfillment == null;
        return testValue.equals(sourceRequestDefaultFulfillment);
    }

    public int getSourceRequestDefaultFulfillmentOrdinal(int ordinal) {
        return sourceRequestDefaultFulfillmentOrdinal;
    }

    public String getRecipeGroups(int ordinal) {
        return recipeGroups;
    }

    public boolean isRecipeGroupsEqual(int ordinal, String testValue) {
        if(testValue == null)
            return recipeGroups == null;
        return testValue.equals(recipeGroups);
    }

    public int getRecipeGroupsOrdinal(int ordinal) {
        return recipeGroupsOrdinal;
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

    public TitleSetupRequirementsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TitleSetupRequirementsTypeAPI) typeAPI;
    }

}