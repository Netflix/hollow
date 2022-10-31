package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ProfileIdTypeAPI extends HollowObjectTypeAPI {

    private final ProfileIdDelegateLookupImpl delegateLookupImpl;

    public ProfileIdTypeAPI(MyNamespaceAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new ProfileIdDelegateLookupImpl(this);
    }

    public int getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleInt("profileId", ordinal, "value");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getValueBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleInt("profileId", ordinal, "value");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public ProfileIdDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public MyNamespaceAPI getAPI() {
        return (MyNamespaceAPI) api;
    }

}