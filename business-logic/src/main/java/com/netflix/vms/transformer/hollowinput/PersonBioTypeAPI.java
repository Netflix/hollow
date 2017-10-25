package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PersonBioTypeAPI extends HollowObjectTypeAPI {

    private final PersonBioDelegateLookupImpl delegateLookupImpl;

    public PersonBioTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "spouses",
            "partners",
            "relationships",
            "currentRelationship",
            "personId",
            "birthDate",
            "deathDate",
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

    public int getRelationshipsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonBio", ordinal, "relationships");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public ListOfStringTypeAPI getRelationshipsTypeAPI() {
        return getAPI().getListOfStringTypeAPI();
    }

    public int getCurrentRelationshipOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonBio", ordinal, "currentRelationship");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getCurrentRelationshipTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getPersonId(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("PersonBio", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("PersonBio", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getBirthDateOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonBio", ordinal, "birthDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public ExplicitDateTypeAPI getBirthDateTypeAPI() {
        return getAPI().getExplicitDateTypeAPI();
    }

    public int getDeathDateOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonBio", ordinal, "deathDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public ExplicitDateTypeAPI getDeathDateTypeAPI() {
        return getAPI().getExplicitDateTypeAPI();
    }

    public int getMovieIdsOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonBio", ordinal, "movieIds");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public ListOfVideoIdsTypeAPI getMovieIdsTypeAPI() {
        return getAPI().getListOfVideoIdsTypeAPI();
    }

    public PersonBioDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}