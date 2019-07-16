package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class TitleSetupRequirementsTemplateTypeAPI extends HollowObjectTypeAPI {

    private final TitleSetupRequirementsTemplateDelegateLookupImpl delegateLookupImpl;

    public TitleSetupRequirementsTemplateTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieType",
            "subtype",
            "active",
            "version",
            "enforcePackageLanguageCheck",
            "collectionRequired",
            "startEndPointRequired",
            "windowStartOffset",
            "ratingReviewRequired",
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
        this.delegateLookupImpl = new TitleSetupRequirementsTemplateDelegateLookupImpl(this);
    }

    public int getMovieTypeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirementsTemplate", ordinal, "movieType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MovieTypeTypeAPI getMovieTypeTypeAPI() {
        return getAPI().getMovieTypeTypeAPI();
    }

    public int getSubtypeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirementsTemplate", ordinal, "subtype");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public SubtypeTypeAPI getSubtypeTypeAPI() {
        return getAPI().getSubtypeTypeAPI();
    }

    public boolean getActive(int ordinal) {
        if(fieldIndex[2] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "active"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]));
    }

    public Boolean getActiveBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "active");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public int getVersion(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleInt("TitleSetupRequirementsTemplate", ordinal, "version");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
    }

    public Integer getVersionBoxed(int ordinal) {
        int i;
        if(fieldIndex[3] == -1) {
            i = missingDataHandler().handleInt("TitleSetupRequirementsTemplate", ordinal, "version");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public boolean getEnforcePackageLanguageCheck(int ordinal) {
        if(fieldIndex[4] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "enforcePackageLanguageCheck"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]));
    }

    public Boolean getEnforcePackageLanguageCheckBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "enforcePackageLanguageCheck");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public boolean getCollectionRequired(int ordinal) {
        if(fieldIndex[5] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "collectionRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]));
    }

    public Boolean getCollectionRequiredBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "collectionRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public boolean getStartEndPointRequired(int ordinal) {
        if(fieldIndex[6] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "startEndPointRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]));
    }

    public Boolean getStartEndPointRequiredBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "startEndPointRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public int getWindowStartOffsetOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirementsTemplate", ordinal, "windowStartOffset");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public LongTypeAPI getWindowStartOffsetTypeAPI() {
        return getAPI().getLongTypeAPI();
    }

    public boolean getRatingReviewRequired(int ordinal) {
        if(fieldIndex[8] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "ratingReviewRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[8]));
    }

    public Boolean getRatingReviewRequiredBoxed(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "ratingReviewRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[8]);
    }



    public boolean getSubsRequired(int ordinal) {
        if(fieldIndex[9] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "subsRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[9]));
    }

    public Boolean getSubsRequiredBoxed(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "subsRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[9]);
    }



    public boolean getDubsRequired(int ordinal) {
        if(fieldIndex[10] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "dubsRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[10]));
    }

    public Boolean getDubsRequiredBoxed(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "dubsRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[10]);
    }



    public boolean getArtworkRequired(int ordinal) {
        if(fieldIndex[11] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "artworkRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[11]));
    }

    public Boolean getArtworkRequiredBoxed(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "artworkRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[11]);
    }



    public boolean getInstreamStillsRequired(int ordinal) {
        if(fieldIndex[12] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "instreamStillsRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[12]));
    }

    public Boolean getInstreamStillsRequiredBoxed(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "instreamStillsRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[12]);
    }



    public boolean getInformativeSynopsisRequired(int ordinal) {
        if(fieldIndex[13] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "informativeSynopsisRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[13]));
    }

    public Boolean getInformativeSynopsisRequiredBoxed(int ordinal) {
        if(fieldIndex[13] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "informativeSynopsisRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[13]);
    }



    public int getRatingsRequiredOrdinal(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirementsTemplate", ordinal, "ratingsRequired");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[14]);
    }

    public RatingsRequirementsTypeAPI getRatingsRequiredTypeAPI() {
        return getAPI().getRatingsRequirementsTypeAPI();
    }

    public boolean getTaggingRequired(int ordinal) {
        if(fieldIndex[15] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "taggingRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[15]));
    }

    public Boolean getTaggingRequiredBoxed(int ordinal) {
        if(fieldIndex[15] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "taggingRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[15]);
    }



    public boolean getCastRequired(int ordinal) {
        if(fieldIndex[16] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "castRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[16]));
    }

    public Boolean getCastRequiredBoxed(int ordinal) {
        if(fieldIndex[16] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "castRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[16]);
    }



    public boolean getDisplayNameRequired(int ordinal) {
        if(fieldIndex[17] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "displayNameRequired"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[17]));
    }

    public Boolean getDisplayNameRequiredBoxed(int ordinal) {
        if(fieldIndex[17] == -1)
            return missingDataHandler().handleBoolean("TitleSetupRequirementsTemplate", ordinal, "displayNameRequired");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[17]);
    }



    public int getSourceRequestDefaultFulfillmentOrdinal(int ordinal) {
        if(fieldIndex[18] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirementsTemplate", ordinal, "sourceRequestDefaultFulfillment");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[18]);
    }

    public SourceRequestDefaultFulfillmentTypeAPI getSourceRequestDefaultFulfillmentTypeAPI() {
        return getAPI().getSourceRequestDefaultFulfillmentTypeAPI();
    }

    public int getRecipeGroupsOrdinal(int ordinal) {
        if(fieldIndex[19] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirementsTemplate", ordinal, "recipeGroups");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[19]);
    }

    public RecipeGroupsTypeAPI getRecipeGroupsTypeAPI() {
        return getAPI().getRecipeGroupsTypeAPI();
    }

    public int getDateCreatedOrdinal(int ordinal) {
        if(fieldIndex[20] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirementsTemplate", ordinal, "dateCreated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[20]);
    }

    public DateTypeAPI getDateCreatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[21] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirementsTemplate", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[21]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getCreatedByOrdinal(int ordinal) {
        if(fieldIndex[22] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirementsTemplate", ordinal, "createdBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[22]);
    }

    public StringTypeAPI getCreatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByOrdinal(int ordinal) {
        if(fieldIndex[23] == -1)
            return missingDataHandler().handleReferencedOrdinal("TitleSetupRequirementsTemplate", ordinal, "updatedBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[23]);
    }

    public StringTypeAPI getUpdatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public TitleSetupRequirementsTemplateDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}