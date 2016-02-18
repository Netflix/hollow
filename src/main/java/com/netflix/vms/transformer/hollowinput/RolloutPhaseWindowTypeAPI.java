package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhaseWindowTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseWindowDelegateLookupImpl delegateLookupImpl;

    RolloutPhaseWindowTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "endDate",
            "startDate"
        });
        this.delegateLookupImpl = new RolloutPhaseWindowDelegateLookupImpl(this);
    }

    public int getEndDateOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseWindow", ordinal, "endDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public DateTypeAPI getEndDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getStartDateOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseWindow", ordinal, "startDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public DateTypeAPI getStartDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public RolloutPhaseWindowDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}