package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RightsTypeAPI extends HollowObjectTypeAPI {

    private final RightsDelegateLookupImpl delegateLookupImpl;

    RightsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "windows",
            "contracts"
        });
        this.delegateLookupImpl = new RightsDelegateLookupImpl(this);
    }

    public int getWindowsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rights", ordinal, "windows");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ListOfRightsWindowTypeAPI getWindowsTypeAPI() {
        return getAPI().getListOfRightsWindowTypeAPI();
    }

    public int getContractsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rights", ordinal, "contracts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ListOfRightsContractTypeAPI getContractsTypeAPI() {
        return getAPI().getListOfRightsContractTypeAPI();
    }

    public RightsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}