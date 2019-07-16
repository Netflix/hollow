package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseDelegate {

    private final RolloutPhaseTypeAPI typeAPI;

    public RolloutPhaseDelegateLookupImpl(RolloutPhaseTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getRolloutPhaseId(int ordinal) {
        return typeAPI.getRolloutPhaseId(ordinal);
    }

    public Long getRolloutPhaseIdBoxed(int ordinal) {
        return typeAPI.getRolloutPhaseIdBoxed(ordinal);
    }

    public String getPhaseName(int ordinal) {
        ordinal = typeAPI.getPhaseNameOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getPhaseNameTypeAPI().getValue(ordinal);
    }

    public boolean isPhaseNameEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getPhaseNameOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getPhaseNameTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getPhaseNameOrdinal(int ordinal) {
        return typeAPI.getPhaseNameOrdinal(ordinal);
    }

    public long getStartDate(int ordinal) {
        ordinal = typeAPI.getStartDateOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getDateTypeAPI().getValue(ordinal);
    }

    public Long getStartDateBoxed(int ordinal) {
        ordinal = typeAPI.getStartDateOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValueBoxed(ordinal);
    }

    public int getStartDateOrdinal(int ordinal) {
        return typeAPI.getStartDateOrdinal(ordinal);
    }

    public long getEndDate(int ordinal) {
        ordinal = typeAPI.getEndDateOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getDateTypeAPI().getValue(ordinal);
    }

    public Long getEndDateBoxed(int ordinal) {
        ordinal = typeAPI.getEndDateOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValueBoxed(ordinal);
    }

    public int getEndDateOrdinal(int ordinal) {
        return typeAPI.getEndDateOrdinal(ordinal);
    }

    public String getWindowType(int ordinal) {
        ordinal = typeAPI.getWindowTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getWindowTypeTypeAPI().get_name(ordinal);
    }

    public boolean isWindowTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getWindowTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getWindowTypeTypeAPI().is_nameEqual(ordinal, testValue);
    }

    public int getWindowTypeOrdinal(int ordinal) {
        return typeAPI.getWindowTypeOrdinal(ordinal);
    }

    public boolean getShowCoreMetadata(int ordinal) {
        return typeAPI.getShowCoreMetadata(ordinal);
    }

    public Boolean getShowCoreMetadataBoxed(int ordinal) {
        return typeAPI.getShowCoreMetadataBoxed(ordinal);
    }

    public boolean getOnHold(int ordinal) {
        return typeAPI.getOnHold(ordinal);
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        return typeAPI.getOnHoldBoxed(ordinal);
    }

    public long getSeasonMovieId(int ordinal) {
        ordinal = typeAPI.getSeasonMovieIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getMovieIdTypeAPI().getValue(ordinal);
    }

    public Long getSeasonMovieIdBoxed(int ordinal) {
        ordinal = typeAPI.getSeasonMovieIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValueBoxed(ordinal);
    }

    public int getSeasonMovieIdOrdinal(int ordinal) {
        return typeAPI.getSeasonMovieIdOrdinal(ordinal);
    }

    public String getPhaseType(int ordinal) {
        ordinal = typeAPI.getPhaseTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getPhaseTypeTypeAPI().get_name(ordinal);
    }

    public boolean isPhaseTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getPhaseTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getPhaseTypeTypeAPI().is_nameEqual(ordinal, testValue);
    }

    public int getPhaseTypeOrdinal(int ordinal) {
        return typeAPI.getPhaseTypeOrdinal(ordinal);
    }

    public int getTrailersOrdinal(int ordinal) {
        return typeAPI.getTrailersOrdinal(ordinal);
    }

    public int getCastMembersOrdinal(int ordinal) {
        return typeAPI.getCastMembersOrdinal(ordinal);
    }

    public int getMetadataElementsOrdinal(int ordinal) {
        return typeAPI.getMetadataElementsOrdinal(ordinal);
    }

    public int getPhaseArtworksOrdinal(int ordinal) {
        return typeAPI.getPhaseArtworksOrdinal(ordinal);
    }

    public int getRequiredImageTypesOrdinal(int ordinal) {
        return typeAPI.getRequiredImageTypesOrdinal(ordinal);
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

    public RolloutPhaseTypeAPI getTypeAPI() {
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