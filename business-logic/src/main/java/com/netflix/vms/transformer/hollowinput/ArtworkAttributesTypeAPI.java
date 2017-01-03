package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ArtworkAttributesTypeAPI extends HollowObjectTypeAPI {

    private final ArtworkAttributesDelegateLookupImpl delegateLookupImpl;

    ArtworkAttributesTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "passthrough",
            "ROLLOUT_EXCLUSIVE"
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

    public String getROLLOUT_EXCLUSIVE(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleString("ArtworkAttributes", ordinal, "ROLLOUT_EXCLUSIVE");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[1]);
    }

    public boolean isROLLOUT_EXCLUSIVEEqual(int ordinal, String testValue) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleStringEquals("ArtworkAttributes", ordinal, "ROLLOUT_EXCLUSIVE", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[1], testValue);
    }

    public ArtworkAttributesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}