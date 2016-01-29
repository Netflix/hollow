package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhasesTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhasesDelegateLookupImpl delegateLookupImpl;

    RolloutPhasesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "seasonMovieId",
            "elements",
            "name",
            "showCoreMetadata",
            "windows",
            "phaseType"
        });
        this.delegateLookupImpl = new RolloutPhasesDelegateLookupImpl(this);
    }

    public long getSeasonMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RolloutPhases", ordinal, "seasonMovieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getSeasonMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RolloutPhases", ordinal, "seasonMovieId");
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
            return missingDataHandler().handleReferencedOrdinal("RolloutPhases", ordinal, "elements");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public RolloutPhasesElementsTypeAPI getElementsTypeAPI() {
        return getAPI().getRolloutPhasesElementsTypeAPI();
    }

    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhases", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getShowCoreMetadata(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("RolloutPhases", ordinal, "showCoreMetadata") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]) == Boolean.TRUE;
    }

    public Boolean getShowCoreMetadataBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("RolloutPhases", ordinal, "showCoreMetadata");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public int getWindowsOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhases", ordinal, "windows");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public RolloutPhasesMapOfWindowsTypeAPI getWindowsTypeAPI() {
        return getAPI().getRolloutPhasesMapOfWindowsTypeAPI();
    }

    public int getPhaseTypeOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhases", ordinal, "phaseType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getPhaseTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public RolloutPhasesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}