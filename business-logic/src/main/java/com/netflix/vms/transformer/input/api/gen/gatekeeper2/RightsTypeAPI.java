package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RightsTypeAPI extends HollowObjectTypeAPI {

    private final RightsDelegateLookupImpl delegateLookupImpl;

    public RightsTypeAPI(Gk2StatusAPI api, HollowObjectTypeDataAccess typeDataAccess) {
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
    public Gk2StatusAPI getAPI() {
        return (Gk2StatusAPI) api;
    }

}