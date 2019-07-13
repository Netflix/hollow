package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PersonVideoAliasIdTypeAPI extends HollowObjectTypeAPI {

    private final PersonVideoAliasIdDelegateLookupImpl delegateLookupImpl;

    public PersonVideoAliasIdTypeAPI(PersonVideoAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new PersonVideoAliasIdDelegateLookupImpl(this);
    }

    public int getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleInt("PersonVideoAliasId", ordinal, "value");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getValueBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleInt("PersonVideoAliasId", ordinal, "value");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public PersonVideoAliasIdDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public PersonVideoAPI getAPI() {
        return (PersonVideoAPI) api;
    }

}