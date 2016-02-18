package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CharacterArtworkAttributesTypeAPI extends HollowObjectTypeAPI {

    private final CharacterArtworkAttributesDelegateLookupImpl delegateLookupImpl;

    CharacterArtworkAttributesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "file_seq"
        });
        this.delegateLookupImpl = new CharacterArtworkAttributesDelegateLookupImpl(this);
    }

    public int getFile_seqOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterArtworkAttributes", ordinal, "file_seq");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getFile_seqTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public CharacterArtworkAttributesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}