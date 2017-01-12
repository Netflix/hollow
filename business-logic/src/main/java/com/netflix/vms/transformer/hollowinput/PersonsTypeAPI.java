package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PersonsTypeAPI extends HollowObjectTypeAPI {

    private final PersonsDelegateLookupImpl delegateLookupImpl;

    PersonsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "personId",
            "name",
            "bio"
        });
        this.delegateLookupImpl = new PersonsDelegateLookupImpl(this);
    }

    public long getPersonId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Persons", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Persons", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Persons", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getBioOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Persons", ordinal, "bio");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public TranslatedTextTypeAPI getBioTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public PersonsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}