package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class PersonAliasesNameTypeAPI extends HollowObjectTypeAPI {

    private final PersonAliasesNameDelegateLookupImpl delegateLookupImpl;

    PersonAliasesNameTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new PersonAliasesNameDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonAliasesName", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public PersonAliasesNameMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getPersonAliasesNameMapOfTranslatedTextsTypeAPI();
    }

    public PersonAliasesNameDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}