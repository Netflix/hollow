package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RolloutTypeAPI extends HollowObjectTypeAPI {

    private final RolloutDelegateLookupImpl delegateLookupImpl;

    public RolloutTypeAPI(RolloutAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "rolloutId",
            "movieId",
            "rolloutName",
            "rolloutType",
            "phases"
        });
        this.delegateLookupImpl = new RolloutDelegateLookupImpl(this);
    }

    public long getRolloutId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Rollout", ordinal, "rolloutId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getRolloutIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Rollout", ordinal, "rolloutId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getMovieId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("Rollout", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("Rollout", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getRolloutNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "rolloutName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getRolloutNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRolloutTypeOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "rolloutType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getRolloutTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getPhasesOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "phases");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public RolloutPhaseListTypeAPI getPhasesTypeAPI() {
        return getAPI().getRolloutPhaseListTypeAPI();
    }

    public RolloutDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public RolloutAPI getAPI() {
        return (RolloutAPI) api;
    }

}