package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhaseLocalizedMetadataTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseLocalizedMetadataDelegateLookupImpl delegateLookupImpl;

    RolloutPhaseLocalizedMetadataTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "SUPPLEMENTAL_MESSAGE",
            "TAGLINE"
        });
        this.delegateLookupImpl = new RolloutPhaseLocalizedMetadataDelegateLookupImpl(this);
    }

    public int getSUPPLEMENTAL_MESSAGEOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseLocalizedMetadata", ordinal, "SUPPLEMENTAL_MESSAGE");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getSUPPLEMENTAL_MESSAGETypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTAGLINEOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseLocalizedMetadata", ordinal, "TAGLINE");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getTAGLINETypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public RolloutPhaseLocalizedMetadataDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}