package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutTypeAPI extends HollowObjectTypeAPI {

    private final RolloutDelegateLookupImpl delegateLookupImpl;

    RolloutTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "rolloutName",
            "launchDates",
            "rolloutId",
            "rolloutType",
            "movieId",
            "phases"
        });
        this.delegateLookupImpl = new RolloutDelegateLookupImpl(this);
    }

    public int getRolloutNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "rolloutName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getRolloutNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getLaunchDatesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "launchDates");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public RolloutMapOfLaunchDatesTypeAPI getLaunchDatesTypeAPI() {
        return getAPI().getRolloutMapOfLaunchDatesTypeAPI();
    }

    public long getRolloutId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("Rollout", ordinal, "rolloutId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getRolloutIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("Rollout", ordinal, "rolloutId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getRolloutTypeOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "rolloutType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getRolloutTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("Rollout", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("Rollout", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getPhasesOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "phases");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public RolloutArrayOfPhasesTypeAPI getPhasesTypeAPI() {
        return getAPI().getRolloutArrayOfPhasesTypeAPI();
    }

    public RolloutDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}