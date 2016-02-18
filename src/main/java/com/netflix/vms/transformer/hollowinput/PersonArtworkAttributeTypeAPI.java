package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class PersonArtworkAttributeTypeAPI extends HollowObjectTypeAPI {

    private final PersonArtworkAttributeDelegateLookupImpl delegateLookupImpl;

    PersonArtworkAttributeTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "file_seq"
        });
        this.delegateLookupImpl = new PersonArtworkAttributeDelegateLookupImpl(this);
    }

    public int getFile_seqOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtworkAttribute", ordinal, "file_seq");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getFile_seqTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public PersonArtworkAttributeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}