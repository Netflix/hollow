package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PhaseTagTypeAPI extends HollowObjectTypeAPI {

    private final PhaseTagDelegateLookupImpl delegateLookupImpl;

    PhaseTagTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "phaseTag",
            "scheduleId"
        });
        this.delegateLookupImpl = new PhaseTagDelegateLookupImpl(this);
    }

    public int getPhaseTagOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("PhaseTag", ordinal, "phaseTag");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getPhaseTagTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getScheduleIdOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("PhaseTag", ordinal, "scheduleId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getScheduleIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public PhaseTagDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}