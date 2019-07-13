package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RolloutPhaseTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseDelegateLookupImpl delegateLookupImpl;

    public RolloutPhaseTypeAPI(RolloutAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "seasonMovieId",
            "elements",
            "name",
            "showCoreMetadata",
            "windows",
            "phaseType",
            "onHold"
        });
        this.delegateLookupImpl = new RolloutPhaseDelegateLookupImpl(this);
    }

    public long getSeasonMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RolloutPhase", ordinal, "seasonMovieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getSeasonMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RolloutPhase", ordinal, "seasonMovieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getElementsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "elements");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public RolloutPhaseElementsTypeAPI getElementsTypeAPI() {
        return getAPI().getRolloutPhaseElementsTypeAPI();
    }

    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getShowCoreMetadata(int ordinal) {
        if(fieldIndex[3] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("RolloutPhase", ordinal, "showCoreMetadata"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]));
    }

    public Boolean getShowCoreMetadataBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("RolloutPhase", ordinal, "showCoreMetadata");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public int getWindowsOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "windows");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public RolloutPhaseWindowMapTypeAPI getWindowsTypeAPI() {
        return getAPI().getRolloutPhaseWindowMapTypeAPI();
    }

    public int getPhaseTypeOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "phaseType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getPhaseTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getOnHold(int ordinal) {
        if(fieldIndex[6] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("RolloutPhase", ordinal, "onHold"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]));
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("RolloutPhase", ordinal, "onHold");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public RolloutPhaseDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public RolloutAPI getAPI() {
        return (RolloutAPI) api;
    }

}