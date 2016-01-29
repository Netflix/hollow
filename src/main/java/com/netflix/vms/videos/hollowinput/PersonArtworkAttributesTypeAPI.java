package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class PersonArtworkAttributesTypeAPI extends HollowObjectTypeAPI {

    private final PersonArtworkAttributesDelegateLookupImpl delegateLookupImpl;

    PersonArtworkAttributesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "file_seq"
        });
        this.delegateLookupImpl = new PersonArtworkAttributesDelegateLookupImpl(this);
    }

    public int getFile_seqOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtworkAttributes", ordinal, "file_seq");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getFile_seqTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public PersonArtworkAttributesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}