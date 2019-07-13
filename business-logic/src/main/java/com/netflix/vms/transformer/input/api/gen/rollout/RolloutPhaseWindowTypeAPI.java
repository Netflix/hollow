package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RolloutPhaseWindowTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseWindowDelegateLookupImpl delegateLookupImpl;

    public RolloutPhaseWindowTypeAPI(RolloutAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "endDate",
            "startDate"
        });
        this.delegateLookupImpl = new RolloutPhaseWindowDelegateLookupImpl(this);
    }

    public long getEndDate(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RolloutPhaseWindow", ordinal, "endDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getEndDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RolloutPhaseWindow", ordinal, "endDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getStartDate(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("RolloutPhaseWindow", ordinal, "startDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getStartDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("RolloutPhaseWindow", ordinal, "startDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public RolloutPhaseWindowDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public RolloutAPI getAPI() {
        return (RolloutAPI) api;
    }

}