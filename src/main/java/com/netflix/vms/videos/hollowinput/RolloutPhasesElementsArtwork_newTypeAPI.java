package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhasesElementsArtwork_newTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhasesElementsArtwork_newDelegateLookupImpl delegateLookupImpl;

    RolloutPhasesElementsArtwork_newTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "sourceFileIds"
        });
        this.delegateLookupImpl = new RolloutPhasesElementsArtwork_newDelegateLookupImpl(this);
    }

    public int getSourceFileIdsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElementsArtwork_new", ordinal, "sourceFileIds");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI getSourceFileIdsTypeAPI() {
        return getAPI().getRolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI();
    }

    public RolloutPhasesElementsArtwork_newDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}