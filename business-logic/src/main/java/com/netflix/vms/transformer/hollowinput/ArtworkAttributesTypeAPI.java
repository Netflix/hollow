package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ArtworkAttributesTypeAPI extends HollowObjectTypeAPI {

    private final ArtworkAttributesDelegateLookupImpl delegateLookupImpl;

    ArtworkAttributesTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "passthrough"
        });
        this.delegateLookupImpl = new ArtworkAttributesDelegateLookupImpl(this);
    }

    public int getPassthroughOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtworkAttributes", ordinal, "passthrough");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public PassthroughDataTypeAPI getPassthroughTypeAPI() {
        return getAPI().getPassthroughDataTypeAPI();
    }

    public ArtworkAttributesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}