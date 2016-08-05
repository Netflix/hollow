package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PersonCharacterTypeAPI extends HollowObjectTypeAPI {

    private final PersonCharacterDelegateLookupImpl delegateLookupImpl;

    PersonCharacterTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "personId",
            "characterId"
        });
        this.delegateLookupImpl = new PersonCharacterDelegateLookupImpl(this);
    }

    public long getPersonId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("PersonCharacter", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("PersonCharacter", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getCharacterId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("PersonCharacter", ordinal, "characterId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("PersonCharacter", ordinal, "characterId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public PersonCharacterDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}