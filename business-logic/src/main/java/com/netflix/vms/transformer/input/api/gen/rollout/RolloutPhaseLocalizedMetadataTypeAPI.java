package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RolloutPhaseLocalizedMetadataTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseLocalizedMetadataDelegateLookupImpl delegateLookupImpl;

    public RolloutPhaseLocalizedMetadataTypeAPI(RolloutAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "SUPPLEMENTAL_MESSAGE",
            "MERCH_OVERRIDE_MESSAGE",
            "POSTPLAY_OVERRIDE_MESSAGE",
            "ODP_OVERRIDE_MESSAGE",
            "POSTPLAY_ALT",
            "POSTPLAY_COMPLETION",
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

    public int getMERCH_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseLocalizedMetadata", ordinal, "MERCH_OVERRIDE_MESSAGE");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getMERCH_OVERRIDE_MESSAGETypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseLocalizedMetadata", ordinal, "POSTPLAY_OVERRIDE_MESSAGE");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getPOSTPLAY_OVERRIDE_MESSAGETypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getODP_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseLocalizedMetadata", ordinal, "ODP_OVERRIDE_MESSAGE");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getODP_OVERRIDE_MESSAGETypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getPOSTPLAY_ALTOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseLocalizedMetadata", ordinal, "POSTPLAY_ALT");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getPOSTPLAY_ALTTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getPOSTPLAY_COMPLETIONOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseLocalizedMetadata", ordinal, "POSTPLAY_COMPLETION");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getPOSTPLAY_COMPLETIONTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTAGLINEOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseLocalizedMetadata", ordinal, "TAGLINE");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public StringTypeAPI getTAGLINETypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public RolloutPhaseLocalizedMetadataDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public RolloutAPI getAPI() {
        return (RolloutAPI) api;
    }

}