package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class BooleanTypeAPI extends HollowObjectTypeAPI {

    private final BooleanDelegateLookupImpl delegateLookupImpl;

    public BooleanTypeAPI(PackageDealCountryAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new BooleanDelegateLookupImpl(this);
    }

    public boolean getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Boolean", ordinal, "value"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]));
    }

    public Boolean getValueBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("Boolean", ordinal, "value");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public BooleanDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public PackageDealCountryAPI getAPI() {
        return (PackageDealCountryAPI) api;
    }

}