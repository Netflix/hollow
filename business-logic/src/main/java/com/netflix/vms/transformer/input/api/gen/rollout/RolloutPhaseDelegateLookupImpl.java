package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseDelegate {

    private final RolloutPhaseTypeAPI typeAPI;

    public RolloutPhaseDelegateLookupImpl(RolloutPhaseTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getSeasonMovieId(int ordinal) {
        return typeAPI.getSeasonMovieId(ordinal);
    }

    public Long getSeasonMovieIdBoxed(int ordinal) {
        return typeAPI.getSeasonMovieIdBoxed(ordinal);
    }

    public int getElementsOrdinal(int ordinal) {
        return typeAPI.getElementsOrdinal(ordinal);
    }

    public String getName(int ordinal) {
        ordinal = typeAPI.getNameOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isNameEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getNameOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getNameOrdinal(int ordinal) {
        return typeAPI.getNameOrdinal(ordinal);
    }

    public boolean getShowCoreMetadata(int ordinal) {
        return typeAPI.getShowCoreMetadata(ordinal);
    }

    public Boolean getShowCoreMetadataBoxed(int ordinal) {
        return typeAPI.getShowCoreMetadataBoxed(ordinal);
    }

    public int getWindowsOrdinal(int ordinal) {
        return typeAPI.getWindowsOrdinal(ordinal);
    }

    public String getPhaseType(int ordinal) {
        ordinal = typeAPI.getPhaseTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isPhaseTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getPhaseTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getPhaseTypeOrdinal(int ordinal) {
        return typeAPI.getPhaseTypeOrdinal(ordinal);
    }

    public boolean getOnHold(int ordinal) {
        return typeAPI.getOnHold(ordinal);
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        return typeAPI.getOnHoldBoxed(ordinal);
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