package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class TitleSetupRequirementsTypeAPI extends HollowObjectTypeAPI {

    private final TitleSetupRequirementsDelegateLookupImpl delegateLookupImpl;

    public TitleSetupRequirementsTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "titleSetupRequirementsTemplate",
            "subsRequired",
            "dubsRequired",
            "artworkRequired",
            "instreamStillsRequired",
            "informativeSynopsisRequired",
            "ratingsRequired",
            "taggingRequired",
            "castRequired",
            "displayNameRequired",
            "sourceRequestDefaultFulfillment",
            "recipeGroups",
            "dateCreated",
            "lastUpdated",
            "createdBy",
            "updatedBy"
        });
        this.delegateLookupImpl = new TitleSetupRequirementsDelegateLookupImpl(this);
    }

    public int getMovieIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirements", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MovieIdTypeAPI getMovieIdTypeAPI() {
        return getAPI().getMovieIdTypeAPI();
    }

    public int getTitleSetupRequirementsTemplateOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirements", ordinal, "titleSetupRequirementsTemplate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TitleSetupRequirementsTemplateTypeAPI getTitleSetupRequirementsTemplateTypeAPI() {
        return getAPI().getTitleSetupRequirementsTemplateTypeAPI();
    }

    public boolean getSubsRequired(int ordinal) {
        if(fieldIndex[2] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "subsRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]));
    }

    public Boolean getSubsRequiredBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "subsRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public boolean getDubsRequired(int ordinal) {
        if(fieldIndex[3] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "dubsRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]));
    }

    public Boolean getDubsRequiredBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "dubsRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public boolean getArtworkRequired(int ordinal) {
        if(fieldIndex[4] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "artworkRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]));
    }

    public Boolean getArtworkRequiredBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "artworkRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public boolean getInstreamStillsRequired(int ordinal) {
        if(fieldIndex[5] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "instreamStillsRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]));
    }

    public Boolean getInstreamStillsRequiredBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "instreamStillsRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public boolean getInformativeSynopsisRequired(int ordinal) {
        if(fieldIndex[6] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "informativeSynopsisRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]));
    }

    public Boolean getInformativeSynopsisRequiredBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "informativeSynopsisRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public int getRatingsRequiredOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirements", ordinal, "ratingsRequired");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public RatingsRequirementsTypeAPI getRatingsRequiredTypeAPI() {
        return getAPI().getRatingsRequirementsTypeAPI();
    }

    public boolean getTaggingRequired(int ordinal) {
        if(fieldIndex[8] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "taggingRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[8]));
    }

    public Boolean getTaggingRequiredBoxed(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "taggingRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[8]);
    }



    public boolean getCastRequired(int ordinal) {
        if(fieldIndex[9] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "castRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[9]));
    }

    public Boolean getCastRequiredBoxed(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "castRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[9]);
    }



    public boolean getDisplayNameRequired(int ordinal) {
        if(fieldIndex[10] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "displayNameRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[10]));
    }

    public Boolean getDisplayNameRequiredBoxed(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirements", ordinal, "displayNameRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[10]);
    }



    public int getSourceRequestDefaultFulfillmentOrdinal(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirements", ordinal, "sourceRequestDefaultFulfillment");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[11]);
    }

    public SourceRequestDefaultFulfillmentTypeAPI getSourceRequestDefaultFulfillmentTypeAPI() {
        return getAPI().getSourceRequestDefaultFulfillmentTypeAPI();
    }

    public int getRecipeGroupsOrdinal(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirements", ordinal, "recipeGroups");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[12]);
    }

    public RecipeGroupsTypeAPI getRecipeGroupsTypeAPI() {
        return getAPI().getRecipeGroupsTypeAPI();
    }

    public int getDateCreatedOrdinal(int ordinal) {
        if(fieldIndex[13] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirements", ordinal, "dateCreated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[13]);
    }

    public DateTypeAPI getDateCreatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirements", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[14]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getCreatedByOrdinal(int ordinal) {
        if(fieldIndex[15] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirements", ordinal, "createdBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[15]);
    }

    public StringTypeAPI getCreatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByOrdinal(int ordinal) {
        if(fieldIndex[16] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirements", ordinal, "updatedBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[16]);
    }

    public StringTypeAPI getUpdatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public TitleSetupRequirementsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}