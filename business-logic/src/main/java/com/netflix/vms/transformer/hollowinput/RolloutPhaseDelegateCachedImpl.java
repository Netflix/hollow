package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseDelegate {

    private final Long seasonMovieId;
    private final int elementsOrdinal;
    private final int nameOrdinal;
    private final Boolean showCoreMetadata;
    private final int windowsOrdinal;
    private final int phaseTypeOrdinal;
    private final Boolean onHold;
   private RolloutPhaseTypeAPI typeAPI;

    public RolloutPhaseDelegateCachedImpl(RolloutPhaseTypeAPI typeAPI, int ordinal) {
        this.seasonMovieId = typeAPI.getSeasonMovieIdBoxed(ordinal);
        this.elementsOrdinal = typeAPI.getElementsOrdinal(ordinal);
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.showCoreMetadata = typeAPI.getShowCoreMetadataBoxed(ordinal);
        this.windowsOrdinal = typeAPI.getWindowsOrdinal(ordinal);
        this.phaseTypeOrdinal = typeAPI.getPhaseTypeOrdinal(ordinal);
        this.onHold = typeAPI.getOnHoldBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getSeasonMovieId(int ordinal) {
        return seasonMovieId.longValue();
    }

    public Long getSeasonMovieIdBoxed(int ordinal) {
        return seasonMovieId;
    }

    public int getElementsOrdinal(int ordinal) {
        return elementsOrdinal;
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    public boolean getShowCoreMetadata(int ordinal) {
        return showCoreMetadata.booleanValue();
    }

    public Boolean getShowCoreMetadataBoxed(int ordinal) {
        return showCoreMetadata;
    }

    public int getWindowsOrdinal(int ordinal) {
        return windowsOrdinal;
    }

    public int getPhaseTypeOrdinal(int ordinal) {
        return phaseTypeOrdinal;
    }

    public boolean getOnHold(int ordinal) {
        return onHold.booleanValue();
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        return onHold;
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