package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

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

    public int getPhaseTypeOrdinal(int ordinal) {
        return typeAPI.getPhaseTypeOrdinal(ordinal);
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