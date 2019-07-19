package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class IsOriginalTitleTypeAPI extends HollowObjectTypeAPI {

    private final IsOriginalTitleDelegateLookupImpl delegateLookupImpl;

    public IsOriginalTitleTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new IsOriginalTitleDelegateLookupImpl(this);
    }

    public boolean getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("isOriginalTitle", ordinal, "value"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]));
    }

    public Boolean getValueBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("isOriginalTitle", ordinal, "value");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public IsOriginalTitleDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}