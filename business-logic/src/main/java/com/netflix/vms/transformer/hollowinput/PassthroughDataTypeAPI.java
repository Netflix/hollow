package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PassthroughDataTypeAPI extends HollowObjectTypeAPI {

    private final PassthroughDataDelegateLookupImpl delegateLookupImpl;

    PassthroughDataTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "singleValues",
            "multiValues"
        });
        this.delegateLookupImpl = new PassthroughDataDelegateLookupImpl(this);
    }

    public int getSingleValuesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("PassthroughData", ordinal, "singleValues");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public SingleValuePassthroughMapTypeAPI getSingleValuesTypeAPI() {
        return getAPI().getSingleValuePassthroughMapTypeAPI();
    }

    public int getMultiValuesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("PassthroughData", ordinal, "multiValues");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public MultiValuePassthroughMapTypeAPI getMultiValuesTypeAPI() {
        return getAPI().getMultiValuePassthroughMapTypeAPI();
    }

    public PassthroughDataDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}