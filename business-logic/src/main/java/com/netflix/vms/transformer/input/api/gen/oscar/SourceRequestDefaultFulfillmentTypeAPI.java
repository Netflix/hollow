package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class SourceRequestDefaultFulfillmentTypeAPI extends HollowObjectTypeAPI {

    private final SourceRequestDefaultFulfillmentDelegateLookupImpl delegateLookupImpl;

    public SourceRequestDefaultFulfillmentTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "_name"
        });
        this.delegateLookupImpl = new SourceRequestDefaultFulfillmentDelegateLookupImpl(this);
    }

    public String get_name(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleString("SourceRequestDefaultFulfillment", ordinal, "_name");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[0]);
    }

    public boolean is_nameEqual(int ordinal, String testValue) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleStringEquals("SourceRequestDefaultFulfillment", ordinal, "_name", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[0], testValue);
    }

    public SourceRequestDefaultFulfillmentDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}