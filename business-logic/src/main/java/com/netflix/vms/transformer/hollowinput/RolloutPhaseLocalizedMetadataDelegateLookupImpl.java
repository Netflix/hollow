package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseLocalizedMetadataDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseLocalizedMetadataDelegate {

    private final RolloutPhaseLocalizedMetadataTypeAPI typeAPI;

    public RolloutPhaseLocalizedMetadataDelegateLookupImpl(RolloutPhaseLocalizedMetadataTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getSUPPLEMENTAL_MESSAGEOrdinal(int ordinal) {
        return typeAPI.getSUPPLEMENTAL_MESSAGEOrdinal(ordinal);
    }

    public int getMERCH_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return typeAPI.getMERCH_OVERRIDE_MESSAGEOrdinal(ordinal);
    }

    public int getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return typeAPI.getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(ordinal);
    }

    public int getODP_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return typeAPI.getODP_OVERRIDE_MESSAGEOrdinal(ordinal);
    }

    public int getTAGLINEOrdinal(int ordinal) {
        return typeAPI.getTAGLINEOrdinal(ordinal);
    }

    public RolloutPhaseLocalizedMetadataTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}