package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PersonVideoTypeAPI extends HollowObjectTypeAPI {

    private final PersonVideoDelegateLookupImpl delegateLookupImpl;

    public PersonVideoTypeAPI(PersonVideoAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "aliasIds",
            "roles",
            "personId"
        });
        this.delegateLookupImpl = new PersonVideoDelegateLookupImpl(this);
    }

    public int getAliasIdsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonVideo", ordinal, "aliasIds");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public PersonVideoAliasIdsListTypeAPI getAliasIdsTypeAPI() {
        return getAPI().getPersonVideoAliasIdsListTypeAPI();
    }

    public int getRolesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonVideo", ordinal, "roles");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public PersonVideoRolesListTypeAPI getRolesTypeAPI() {
        return getAPI().getPersonVideoRolesListTypeAPI();
    }

    public long getPersonId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("PersonVideo", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("PersonVideo", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public PersonVideoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public PersonVideoAPI getAPI() {
        return (PersonVideoAPI) api;
    }

}