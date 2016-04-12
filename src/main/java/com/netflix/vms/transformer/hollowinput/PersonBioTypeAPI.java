package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class PersonBioTypeAPI extends HollowObjectTypeAPI {

    private final PersonBioDelegateLookupImpl delegateLookupImpl;

    PersonBioTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "spouses",
            "partners",
            "personId",
            "birthDate",
            "movieIds"
        });
        this.delegateLookupImpl = new PersonBioDelegateLookupImpl(this);
    }

    public int getSpousesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonBio", ordinal, "spouses");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ListOfStringTypeAPI getSpousesTypeAPI() {
        return getAPI().getListOfStringTypeAPI();
    }

    public int getPartnersOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonBio", ordinal, "partners");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ListOfStringTypeAPI getPartnersTypeAPI() {
        return getAPI().getListOfStringTypeAPI();
    }

    public long getPersonId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("PersonBio", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("PersonBio", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getBirthDateOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonBio", ordinal, "birthDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public ExplicitDateTypeAPI getBirthDateTypeAPI() {
        return getAPI().getExplicitDateTypeAPI();
    }

    public int getMovieIdsOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonBio", ordinal, "movieIds");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public ListOfVideoIdsTypeAPI getMovieIdsTypeAPI() {
        return getAPI().getListOfVideoIdsTypeAPI();
    }

    public PersonBioDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}