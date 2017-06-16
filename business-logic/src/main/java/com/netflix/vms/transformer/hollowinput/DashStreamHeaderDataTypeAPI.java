package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class DashStreamHeaderDataTypeAPI extends HollowObjectTypeAPI {

    private final DashStreamHeaderDataDelegateLookupImpl delegateLookupImpl;

    DashStreamHeaderDataTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "boxInfo"
        });
        this.delegateLookupImpl = new DashStreamHeaderDataDelegateLookupImpl(this);
    }

    public int getBoxInfoOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("DashStreamHeaderData", ordinal, "boxInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public SetOfStreamBoxInfoTypeAPI getBoxInfoTypeAPI() {
        return getAPI().getSetOfStreamBoxInfoTypeAPI();
    }

    public DashStreamHeaderDataDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}