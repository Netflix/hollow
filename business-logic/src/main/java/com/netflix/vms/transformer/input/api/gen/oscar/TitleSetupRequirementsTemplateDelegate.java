package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface TitleSetupRequirementsTemplateDelegate extends HollowObjectDelegate {

    public int getMovieTypeOrdinal(int ordinal);

    public int getSubtypeOrdinal(int ordinal);

    public boolean getActive(int ordinal);

    public Boolean getActiveBoxed(int ordinal);

    public int getVersion(int ordinal);

    public Integer getVersionBoxed(int ordinal);

    public boolean getEnforcePackageLanguageCheck(int ordinal);

    public Boolean getEnforcePackageLanguageCheckBoxed(int ordinal);

    public boolean getCollectionRequired(int ordinal);

    public Boolean getCollectionRequiredBoxed(int ordinal);

    public boolean getStartEndPointRequired(int ordinal);

    public Boolean getStartEndPointRequiredBoxed(int ordinal);

    public long getWindowStartOffset(int ordinal);

    public Long getWindowStartOffsetBoxed(int ordinal);

    public int getWindowStartOffsetOrdinal(int ordinal);

    public boolean getRatingReviewRequired(int ordinal);

    public Boolean getRatingReviewRequiredBoxed(int ordinal);

    public boolean getSubsRequired(int ordinal);

    public Boolean getSubsRequiredBoxed(int ordinal);

    public boolean getDubsRequired(int ordinal);

    public Boolean getDubsRequiredBoxed(int ordinal);

    public boolean getArtworkRequired(int ordinal);

    public Boolean getArtworkRequiredBoxed(int ordinal);

    public boolean getInstreamStillsRequired(int ordinal);

    public Boolean getInstreamStillsRequiredBoxed(int ordinal);

    public boolean getInformativeSynopsisRequired(int ordinal);

    public Boolean getInformativeSynopsisRequiredBoxed(int ordinal);

    public String getRatingsRequired(int ordinal);

    public boolean isRatingsRequiredEqual(int ordinal, String testValue);

    public int getRatingsRequiredOrdinal(int ordinal);

    public boolean getTaggingRequired(int ordinal);

    public Boolean getTaggingRequiredBoxed(int ordinal);

    public boolean getCastRequired(int ordinal);

    public Boolean getCastRequiredBoxed(int ordinal);

    public boolean getDisplayNameRequired(int ordinal);

    public Boolean getDisplayNameRequiredBoxed(int ordinal);

    public String getSourceRequestDefaultFulfillment(int ordinal);

    public boolean isSourceRequestDefaultFulfillmentEqual(int ordinal, String testValue);

    public int getSourceRequestDefaultFulfillmentOrdinal(int ordinal);

    public String getRecipeGroups(int ordinal);

    public boolean isRecipeGroupsEqual(int ordinal, String testValue);

    public int getRecipeGroupsOrdinal(int ordinal);

    public long getDateCreated(int ordinal);

    public Long getDateCreatedBoxed(int ordinal);

    public int getDateCreatedOrdinal(int ordinal);

    public long getLastUpdated(int ordinal);

    public Long getLastUpdatedBoxed(int ordinal);

    public int getLastUpdatedOrdinal(int ordinal);

    public String getCreatedBy(int ordinal);

    public boolean isCreatedByEqual(int ordinal, String testValue);

    public int getCreatedByOrdinal(int ordinal);

    public String getUpdatedBy(int ordinal);

    public boolean isUpdatedByEqual(int ordinal, String testValue);

    public int getUpdatedByOrdinal(int ordinal);

    public TitleSetupRequirementsTemplateTypeAPI getTypeAPI();

}