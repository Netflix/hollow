package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhasesElementsCastTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhasesElementsCastDelegateLookupImpl delegateLookupImpl;

    RolloutPhasesElementsCastTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "sequenceNumber",
            "personId"
        });
        this.delegateLookupImpl = new RolloutPhasesElementsCastDelegateLookupImpl(this);
    }

    public long getSequenceNumber(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RolloutPhasesElementsCast", ordinal, "sequenceNumber");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RolloutPhasesElementsCast", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getPersonId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("RolloutPhasesElementsCast", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("RolloutPhasesElementsCast", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public RolloutPhasesElementsCastDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}