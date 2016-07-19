package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RightsWindowTypeAPI extends HollowObjectTypeAPI {

    private final RightsWindowDelegateLookupImpl delegateLookupImpl;

    RightsWindowTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "startDate",
            "endDate",
            "contractIds",
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



    public int getContractIdsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("RightsWindow", ordinal, "contractIds");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public ListOfContractIdTypeAPI getContractIdsTypeAPI() {
        return getAPI().getListOfContractIdTypeAPI();
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
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}