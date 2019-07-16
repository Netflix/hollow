package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class RolloutPhaseDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseDelegate {

    private final Long rolloutPhaseId;
    private final String phaseName;
    private final int phaseNameOrdinal;
    private final Long startDate;
    private final int startDateOrdinal;
    private final Long endDate;
    private final int endDateOrdinal;
    private final String windowType;
    private final int windowTypeOrdinal;
    private final Boolean showCoreMetadata;
    private final Boolean onHold;
    private final Long seasonMovieId;
    private final int seasonMovieIdOrdinal;
    private final String phaseType;
    private final int phaseTypeOrdinal;
    private final int trailersOrdinal;
    private final int castMembersOrdinal;
    private final int metadataElementsOrdinal;
    private final int phaseArtworksOrdinal;
    private final int requiredImageTypesOrdinal;
    private final Long dateCreated;
    private final int dateCreatedOrdinal;
    private final Long lastUpdated;
    private final int lastUpdatedOrdinal;
    private final String createdBy;
    private final int createdByOrdinal;
    private final String updatedBy;
    private final int updatedByOrdinal;
    private RolloutPhaseTypeAPI typeAPI;

    public RolloutPhaseDelegateCachedImpl(RolloutPhaseTypeAPI typeAPI, int ordinal) {
        this.rolloutPhaseId = typeAPI.getRolloutPhaseIdBoxed(ordinal);
        this.phaseNameOrdinal = typeAPI.getPhaseNameOrdinal(ordinal);
        int phaseNameTempOrdinal = phaseNameOrdinal;
        this.phaseName = phaseNameTempOrdinal == -1 ? null : typeAPI.getAPI().getPhaseNameTypeAPI().getValue(phaseNameTempOrdinal);
        this.startDateOrdinal = typeAPI.getStartDateOrdinal(ordinal);
        int startDateTempOrdinal = startDateOrdinal;
        this.startDate = startDateTempOrdinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValue(startDateTempOrdinal);
        this.endDateOrdinal = typeAPI.getEndDateOrdinal(ordinal);
        int endDateTempOrdinal = endDateOrdinal;
        this.endDate = endDateTempOrdinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValue(endDateTempOrdinal);
        this.windowTypeOrdinal = typeAPI.getWindowTypeOrdinal(ordinal);
        int windowTypeTempOrdinal = windowTypeOrdinal;
        this.windowType = windowTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getWindowTypeTypeAPI().get_name(windowTypeTempOrdinal);
        this.showCoreMetadata = typeAPI.getShowCoreMetadataBoxed(ordinal);
        this.onHold = typeAPI.getOnHoldBoxed(ordinal);
        this.seasonMovieIdOrdinal = typeAPI.getSeasonMovieIdOrdinal(ordinal);
        int seasonMovieIdTempOrdinal = seasonMovieIdOrdinal;
        this.seasonMovieId = seasonMovieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValue(seasonMovieIdTempOrdinal);
        this.phaseTypeOrdinal = typeAPI.getPhaseTypeOrdinal(ordinal);
        int phaseTypeTempOrdinal = phaseTypeOrdinal;
        this.phaseType = phaseTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getPhaseTypeTypeAPI().get_name(phaseTypeTempOrdinal);
        this.trailersOrdinal = typeAPI.getTrailersOrdinal(ordinal);
        this.castMembersOrdinal = typeAPI.getCastMembersOrdinal(ordinal);
        this.metadataElementsOrdinal = typeAPI.getMetadataElementsOrdinal(ordinal);
        this.phaseArtworksOrdinal = typeAPI.getPhaseArtworksOrdinal(ordinal);
        this.requiredImageTypesOrdinal = typeAPI.getRequiredImageTypesOrdinal(ordinal);
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

    public long getRolloutPhaseId(int ordinal) {
        if(rolloutPhaseId == null)
            return Long.MIN_VALUE;
        return rolloutPhaseId.longValue();
    }

    public Long getRolloutPhaseIdBoxed(int ordinal) {
        return rolloutPhaseId;
    }

    public String getPhaseName(int ordinal) {
        return phaseName;
    }

    public boolean isPhaseNameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return phaseName == null;
        return testValue.equals(phaseName);
    }

    public int getPhaseNameOrdinal(int ordinal) {
        return phaseNameOrdinal;
    }

    public long getStartDate(int ordinal) {
        if(startDate == null)
            return Long.MIN_VALUE;
        return startDate.longValue();
    }

    public Long getStartDateBoxed(int ordinal) {
        return startDate;
    }

    public int getStartDateOrdinal(int ordinal) {
        return startDateOrdinal;
    }

    public long getEndDate(int ordinal) {
        if(endDate == null)
            return Long.MIN_VALUE;
        return endDate.longValue();
    }

    public Long getEndDateBoxed(int ordinal) {
        return endDate;
    }

    public int getEndDateOrdinal(int ordinal) {
        return endDateOrdinal;
    }

    public String getWindowType(int ordinal) {
        return windowType;
    }

    public boolean isWindowTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return windowType == null;
        return testValue.equals(windowType);
    }

    public int getWindowTypeOrdinal(int ordinal) {
        return windowTypeOrdinal;
    }

    public boolean getShowCoreMetadata(int ordinal) {
        if(showCoreMetadata == null)
            return false;
        return showCoreMetadata.booleanValue();
    }

    public Boolean getShowCoreMetadataBoxed(int ordinal) {
        return showCoreMetadata;
    }

    public boolean getOnHold(int ordinal) {
        if(onHold == null)
            return false;
        return onHold.booleanValue();
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        return onHold;
    }

    public long getSeasonMovieId(int ordinal) {
        if(seasonMovieId == null)
            return Long.MIN_VALUE;
        return seasonMovieId.longValue();
    }

    public Long getSeasonMovieIdBoxed(int ordinal) {
        return seasonMovieId;
    }

    public int getSeasonMovieIdOrdinal(int ordinal) {
        return seasonMovieIdOrdinal;
    }

    public String getPhaseType(int ordinal) {
        return phaseType;
    }

    public boolean isPhaseTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return phaseType == null;
        return testValue.equals(phaseType);
    }

    public int getPhaseTypeOrdinal(int ordinal) {
        return phaseTypeOrdinal;
    }

    public int getTrailersOrdinal(int ordinal) {
        return trailersOrdinal;
    }

    public int getCastMembersOrdinal(int ordinal) {
        return castMembersOrdinal;
    }

    public int getMetadataElementsOrdinal(int ordinal) {
        return metadataElementsOrdinal;
    }

    public int getPhaseArtworksOrdinal(int ordinal) {
        return phaseArtworksOrdinal;
    }

    public int getRequiredImageTypesOrdinal(int ordinal) {
        return requiredImageTypesOrdinal;
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

    public RolloutPhaseTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhaseTypeAPI) typeAPI;
    }

}