package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhaseNewArtworkTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseNewArtworkDelegateLookupImpl delegateLookupImpl;

    RolloutPhaseNewArtworkTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "sourceFileIds"
        });
        this.delegateLookupImpl = new RolloutPhaseNewArtworkDelegateLookupImpl(this);
    }

    public int getSourceFileIdsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseNewArtwork", ordinal, "sourceFileIds");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public RolloutPhaseArtworkSourceFileIdListTypeAPI getSourceFileIdsTypeAPI() {
        return getAPI().getRolloutPhaseArtworkSourceFileIdListTypeAPI();
    }

    public RolloutPhaseNewArtworkDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}