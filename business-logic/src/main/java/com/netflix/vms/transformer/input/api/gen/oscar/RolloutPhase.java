package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhase extends HollowObject {

    public RolloutPhase(RolloutPhaseDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getRolloutPhaseId() {
        return delegate().getRolloutPhaseId(ordinal);
    }

    public Long getRolloutPhaseIdBoxed() {
        return delegate().getRolloutPhaseIdBoxed(ordinal);
    }

    public String getPhaseName() {
        return delegate().getPhaseName(ordinal);
    }

    public boolean isPhaseNameEqual(String testValue) {
        return delegate().isPhaseNameEqual(ordinal, testValue);
    }

    public PhaseName getPhaseNameHollowReference() {
        int refOrdinal = delegate().getPhaseNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPhaseName(refOrdinal);
    }

    public Long getStartDateBoxed() {
        return delegate().getStartDateBoxed(ordinal);
    }

    public long getStartDate() {
        return delegate().getStartDate(ordinal);
    }

    public Date getStartDateHollowReference() {
        int refOrdinal = delegate().getStartDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public Long getEndDateBoxed() {
        return delegate().getEndDateBoxed(ordinal);
    }

    public long getEndDate() {
        return delegate().getEndDate(ordinal);
    }

    public Date getEndDateHollowReference() {
        int refOrdinal = delegate().getEndDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public String getWindowType() {
        return delegate().getWindowType(ordinal);
    }

    public boolean isWindowTypeEqual(String testValue) {
        return delegate().isWindowTypeEqual(ordinal, testValue);
    }

    public WindowType getWindowTypeHollowReference() {
        int refOrdinal = delegate().getWindowTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getWindowType(refOrdinal);
    }

    public boolean getShowCoreMetadata() {
        return delegate().getShowCoreMetadata(ordinal);
    }

    public Boolean getShowCoreMetadataBoxed() {
        return delegate().getShowCoreMetadataBoxed(ordinal);
    }

    public boolean getOnHold() {
        return delegate().getOnHold(ordinal);
    }

    public Boolean getOnHoldBoxed() {
        return delegate().getOnHoldBoxed(ordinal);
    }

    public Long getSeasonMovieIdBoxed() {
        return delegate().getSeasonMovieIdBoxed(ordinal);
    }

    public long getSeasonMovieId() {
        return delegate().getSeasonMovieId(ordinal);
    }

    public MovieId getSeasonMovieIdHollowReference() {
        int refOrdinal = delegate().getSeasonMovieIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieId(refOrdinal);
    }

    public String getPhaseType() {
        return delegate().getPhaseType(ordinal);
    }

    public boolean isPhaseTypeEqual(String testValue) {
        return delegate().isPhaseTypeEqual(ordinal, testValue);
    }

    public PhaseType getPhaseTypeHollowReference() {
        int refOrdinal = delegate().getPhaseTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPhaseType(refOrdinal);
    }

    public SetOfPhaseTrailer getTrailers() {
        int refOrdinal = delegate().getTrailersOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfPhaseTrailer(refOrdinal);
    }

    public SetOfPhaseCastMember getCastMembers() {
        int refOrdinal = delegate().getCastMembersOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfPhaseCastMember(refOrdinal);
    }

    public SetOfPhaseMetadataElement getMetadataElements() {
        int refOrdinal = delegate().getMetadataElementsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfPhaseMetadataElement(refOrdinal);
    }

    public SetOfPhaseArtwork getPhaseArtworks() {
        int refOrdinal = delegate().getPhaseArtworksOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfPhaseArtwork(refOrdinal);
    }

    public SetOfPhaseRequiredImageType getRequiredImageTypes() {
        int refOrdinal = delegate().getRequiredImageTypesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfPhaseRequiredImageType(refOrdinal);
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

    public RolloutPhaseTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseDelegate delegate() {
        return (RolloutPhaseDelegate)delegate;
    }

}