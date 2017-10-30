package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class DerivativeTagTypeAPI extends HollowObjectTypeAPI {

    private final DerivativeTagDelegateLookupImpl delegateLookupImpl;

    public DerivativeTagTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new DerivativeTagDelegateLookupImpl(this);
    }

    public String getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleString("DerivativeTag", ordinal, "value");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[0]);
    }

    public boolean isValueEqual(int ordinal, String testValue) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleStringEquals("DerivativeTag", ordinal, "value", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[0], testValue);
    }

    public DerivativeTagDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}