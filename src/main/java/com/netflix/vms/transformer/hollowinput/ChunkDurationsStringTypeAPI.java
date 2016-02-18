package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ChunkDurationsStringTypeAPI extends HollowObjectTypeAPI {

    private final ChunkDurationsStringDelegateLookupImpl delegateLookupImpl;

    ChunkDurationsStringTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new ChunkDurationsStringDelegateLookupImpl(this);
    }

    public String getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleString("ChunkDurationsString", ordinal, "value");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[0]);
    }

    public boolean isValueEqual(int ordinal, String testValue) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleStringEquals("ChunkDurationsString", ordinal, "value", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[0], testValue);
    }

    public ChunkDurationsStringDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}