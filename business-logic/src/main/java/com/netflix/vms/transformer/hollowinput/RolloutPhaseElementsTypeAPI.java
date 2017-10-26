package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RolloutPhaseElementsTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseElementsDelegateLookupImpl delegateLookupImpl;

    public RolloutPhaseElementsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "localized_metadata",
            "artwork"
        });
        this.delegateLookupImpl = new RolloutPhaseElementsDelegateLookupImpl(this);
    }

    public int getLocalized_metadataOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseElements", ordinal, "localized_metadata");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public RolloutPhaseLocalizedMetadataTypeAPI getLocalized_metadataTypeAPI() {
        return getAPI().getRolloutPhaseLocalizedMetadataTypeAPI();
    }

    public int getArtworkOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseElements", ordinal, "artwork");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public RolloutPhaseArtworkTypeAPI getArtworkTypeAPI() {
        return getAPI().getRolloutPhaseArtworkTypeAPI();
    }

    public RolloutPhaseElementsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}