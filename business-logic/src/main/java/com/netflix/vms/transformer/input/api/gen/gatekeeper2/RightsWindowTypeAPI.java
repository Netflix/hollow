package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RightsWindowTypeAPI extends HollowObjectTypeAPI {

    private final RightsWindowDelegateLookupImpl delegateLookupImpl;

    public RightsWindowTypeAPI(Gk2StatusAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "startDate",
            "endDate",
            "onHold",
            "contractIdsExt"
        });
        this.delegateLookupImpl = new RightsWindowDelegateLookupImpl(this);
    }

    public long getStartDate(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RightsWindow", ordinal, "startDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getStartDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RightsWindow", ordinal, "startDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getEndDate(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("RightsWindow", ordinal, "endDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getEndDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("RightsWindow", ordinal, "endDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getOnHold(int ordinal) {
        if(fieldIndex[2] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("RightsWindow", ordinal, "onHold"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]));
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("RightsWindow", ordinal, "onHold");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public int getContractIdsExtOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("RightsWindow", ordinal, "contractIdsExt");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public ListOfRightsWindowContractTypeAPI getContractIdsExtTypeAPI() {
        return getAPI().getListOfRightsWindowContractTypeAPI();
    }

    public RightsWindowDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public Gk2StatusAPI getAPI() {
        return (Gk2StatusAPI) api;
    }

}