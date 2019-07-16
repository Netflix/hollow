package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TitleSetupRequirements extends HollowObject {

    public TitleSetupRequirements(TitleSetupRequirementsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public MovieId getMovieIdHollowReference() {
        int refOrdinal = delegate().getMovieIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieId(refOrdinal);
    }

    public TitleSetupRequirementsTemplate getTitleSetupRequirementsTemplate() {
        int refOrdinal = delegate().getTitleSetupRequirementsTemplateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTitleSetupRequirementsTemplate(refOrdinal);
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

    public TitleSetupRequirementsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TitleSetupRequirementsDelegate delegate() {
        return (TitleSetupRequirementsDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code TitleSetupRequirements} that has a primary key.
     * The primary key is represented by the type {@code long}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<TitleSetupRequirements, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, TitleSetupRequirements.class)
            .bindToPrimaryKey()
            .usingPath("movieId", long.class);
    }

}