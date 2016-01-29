package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhasesElementsLocalized_metadataTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhasesElementsLocalized_metadataDelegateLookupImpl delegateLookupImpl;

    RolloutPhasesElementsLocalized_metadataTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "SUPPLEMENTAL_MESSAGE",
            "TAGLINE"
        });
        this.delegateLookupImpl = new RolloutPhasesElementsLocalized_metadataDelegateLookupImpl(this);
    }

    public int getSUPPLEMENTAL_MESSAGEOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElementsLocalized_metadata", ordinal, "SUPPLEMENTAL_MESSAGE");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getSUPPLEMENTAL_MESSAGETypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTAGLINEOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElementsLocalized_metadata", ordinal, "TAGLINE");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getTAGLINETypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public RolloutPhasesElementsLocalized_metadataDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}