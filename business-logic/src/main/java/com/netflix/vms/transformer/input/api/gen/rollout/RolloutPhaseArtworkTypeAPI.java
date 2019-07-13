package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RolloutPhaseArtworkTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseArtworkDelegateLookupImpl delegateLookupImpl;

    public RolloutPhaseArtworkTypeAPI(RolloutAPI api, HollowObjectTypeDataAccess typeDataAccess) {
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
    public RolloutAPI getAPI() {
        return (RolloutAPI) api;
    }

}