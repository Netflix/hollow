package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class PersonsTypeAPI extends HollowObjectTypeAPI {

    private final PersonsDelegateLookupImpl delegateLookupImpl;

    PersonsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "name",
            "bio",
            "personId"
        });
        this.delegateLookupImpl = new PersonsDelegateLookupImpl(this);
    }

    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Persons", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public PersonsNameTypeAPI getNameTypeAPI() {
        return getAPI().getPersonsNameTypeAPI();
    }

    public int getBioOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Persons", ordinal, "bio");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public PersonsBioTypeAPI getBioTypeAPI() {
        return getAPI().getPersonsBioTypeAPI();
    }

    public long getPersonId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("Persons", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("Persons", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public PersonsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}