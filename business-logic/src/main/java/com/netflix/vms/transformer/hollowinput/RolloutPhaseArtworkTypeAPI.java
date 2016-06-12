package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhaseArtworkTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseArtworkDelegateLookupImpl delegateLookupImpl;

    RolloutPhaseArtworkTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "sourceFileIds"
        });
        this.delegateLookupImpl = new RolloutPhaseArtworkDelegateLookupImpl(this);
    }

    public int getSourceFileIdsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseArtwork", ordinal, "sourceFileIds");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public RolloutPhaseArtworkSourceFileIdListTypeAPI getSourceFileIdsTypeAPI() {
        return getAPI().getRolloutPhaseArtworkSourceFileIdListTypeAPI();
    }

    public RolloutPhaseArtworkDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}