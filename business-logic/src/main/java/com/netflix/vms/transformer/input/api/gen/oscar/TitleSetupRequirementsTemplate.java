package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TitleSetupRequirementsTemplate extends HollowObject {

    public TitleSetupRequirementsTemplate(TitleSetupRequirementsTemplateDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public MovieType getMovieType() {
        int refOrdinal = delegate().getMovieTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieType(refOrdinal);
    }

    public Subtype getSubtype() {
        int refOrdinal = delegate().getSubtypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSubtype(refOrdinal);
    }

    public boolean getActive() {
        return delegate().getActive(ordinal);
    }

    public Boolean getActiveBoxed() {
        return delegate().getActiveBoxed(ordinal);
    }

    public int getVersion() {
        return delegate().getVersion(ordinal);
    }

    public Integer getVersionBoxed() {
        return delegate().getVersionBoxed(ordinal);
    }

    public boolean getEnforcePackageLanguageCheck() {
        return delegate().getEnforcePackageLanguageCheck(ordinal);
    }

    public Boolean getEnforcePackageLanguageCheckBoxed() {
        return delegate().getEnforcePackageLanguageCheckBoxed(ordinal);
    }

    public boolean getCollectionRequired() {
        return delegate().getCollectionRequired(ordinal);
    }

    public Boolean getCollectionRequiredBoxed() {
        return delegate().getCollectionRequiredBoxed(ordinal);
    }

    public boolean getStartEndPointRequired() {
        return delegate().getStartEndPointRequired(ordinal);
    }

    public Boolean getStartEndPointRequiredBoxed() {
        return delegate().getStartEndPointRequiredBoxed(ordinal);
    }

    public Long getWindowStartOffsetBoxed() {
        return delegate().getWindowStartOffsetBoxed(ordinal);
    }

    public long getWindowStartOffset() {
        return delegate().getWindowStartOffset(ordinal);
    }

    public HLong getWindowStartOffsetHollowReference() {
        int refOrdinal = delegate().getWindowStartOffsetOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHLong(refOrdinal);
    }

    public boolean getRatingReviewRequired() {
        return delegate().getRatingReviewRequired(ordinal);
    }

    public Boolean getRatingReviewRequiredBoxed() {
        return delegate().getRatingReviewRequiredBoxed(ordinal);
    }

    public boolean getSubsRequired() {
        return delegate().getSubsRequired(ordinal);
    }

    public Boolean getSubsRequiredBoxed() {
        return delegate().getSubsRequiredBoxed(ordinal);
    }

    public boolean getDubsRequired() {
        return delegate().getDubsRequired(ordinal);
    }

    public Boolean getDubsRequiredBoxed() {
        return delegate().getDubsRequiredBoxed(ordinal);
    }

    public boolean getArtworkRequired() {
        return delegate().getArtworkRequired(ordinal);
    }

    public Boolean getArtworkRequiredBoxed() {
        return delegate().getArtworkRequiredBoxed(ordinal);
    }

    public boolean getInstreamStillsRequired() {
        return delegate().getInstreamStillsRequired(ordinal);
    }

    public Boolean getInstreamStillsRequiredBoxed() {
        return delegate().getInstreamStillsRequiredBoxed(ordinal);
    }

    public boolean getInformativeSynopsisRequired() {
        return delegate().getInformativeSynopsisRequired(ordinal);
    }

    public Boolean getInformativeSynopsisRequiredBoxed() {
        return delegate().getInformativeSynopsisRequiredBoxed(ordinal);
    }

    public String getRatingsRequired() {
        return delegate().getRatingsRequired(ordinal);
    }

    public boolean isRatingsRequiredEqual(String testValue) {
        return delegate().isRatingsRequiredEqual(ordinal, testValue);
    }

    public RatingsRequirements getRatingsRequiredHollowReference() {
        int refOrdinal = delegate().getRatingsRequiredOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRatingsRequirements(refOrdinal);
    }

    public boolean getTaggingRequired() {
        return delegate().getTaggingRequired(ordinal);
    }

    public Boolean getTaggingRequiredBoxed() {
        return delegate().getTaggingRequiredBoxed(ordinal);
    }

    public boolean getCastRequired() {
        return delegate().getCastRequired(ordinal);
    }

    public Boolean getCastRequiredBoxed() {
        return delegate().getCastRequiredBoxed(ordinal);
    }

    public boolean getDisplayNameRequired() {
        return delegate().getDisplayNameRequired(ordinal);
    }

    public Boolean getDisplayNameRequiredBoxed() {
        return delegate().getDisplayNameRequiredBoxed(ordinal);
    }

    public String getSourceRequestDefaultFulfillment() {
        return delegate().getSourceRequestDefaultFulfillment(ordinal);
    }

    public boolean isSourceRequestDefaultFulfillmentEqual(String testValue) {
        return delegate().isSourceRequestDefaultFulfillmentEqual(ordinal, testValue);
    }

    public SourceRequestDefaultFulfillment getSourceRequestDefaultFulfillmentHollowReference() {
        int refOrdinal = delegate().getSourceRequestDefaultFulfillmentOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSourceRequestDefaultFulfillment(refOrdinal);
    }

    public String getRecipeGroups() {
        return delegate().getRecipeGroups(ordinal);
    }

    public boolean isRecipeGroupsEqual(String testValue) {
        return delegate().isRecipeGroupsEqual(ordinal, testValue);
    }

    public RecipeGroups getRecipeGroupsHollowReference() {
        int refOrdinal = delegate().getRecipeGroupsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRecipeGroups(refOrdinal);
    }

    public Long getDateCreatedBoxed() {
        return delegate().getDateCreatedBoxed(ordinal);
    }

    public long getDateCreated() {
        return delegate().getDateCreated(ordinal);
    }

    public Date getDateCreatedHollowReference() {
        int refOrdinal = delegate().getDateCreatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public Long getLastUpdatedBoxed() {
        return delegate().getLastUpdatedBoxed(ordinal);
    }

    public long getLastUpdated() {
        return delegate().getLastUpdated(ordinal);
    }

    public Date getLastUpdatedHollowReference() {
        int refOrdinal = delegate().getLastUpdatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public String getCreatedBy() {
        return delegate().getCreatedBy(ordinal);
    }

    public boolean isCreatedByEqual(String testValue) {
        return delegate().isCreatedByEqual(ordinal, testValue);
    }

    public HString getCreatedByHollowReference() {
        int refOrdinal = delegate().getCreatedByOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getUpdatedBy() {
        return delegate().getUpdatedBy(ordinal);
    }

    public boolean isUpdatedByEqual(String testValue) {
        return delegate().isUpdatedByEqual(ordinal, testValue);
    }

    public HString getUpdatedByHollowReference() {
        int refOrdinal = delegate().getUpdatedByOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public TitleSetupRequirementsTemplateTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TitleSetupRequirementsTemplateDelegate delegate() {
        return (TitleSetupRequirementsTemplateDelegate)delegate;
    }

}