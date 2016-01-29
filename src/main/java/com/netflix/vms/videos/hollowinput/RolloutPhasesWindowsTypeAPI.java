package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhasesWindowsTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhasesWindowsDelegateLookupImpl delegateLookupImpl;

    RolloutPhasesWindowsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "endDate",
            "startDate"
        });
        this.delegateLookupImpl = new RolloutPhasesWindowsDelegateLookupImpl(this);
    }

    public long getEndDate(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RolloutPhasesWindows", ordinal, "endDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getEndDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RolloutPhasesWindows", ordinal, "endDate");
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
            return missingDataHandler().handleLong("RolloutPhasesWindows", ordinal, "startDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getStartDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("RolloutPhasesWindows", ordinal, "startDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public RolloutPhasesWindowsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}