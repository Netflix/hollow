package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RightsTypeAPI extends HollowObjectTypeAPI {

    private final RightsDelegateLookupImpl delegateLookupImpl;

    RightsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "windows"
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

    public RightsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}