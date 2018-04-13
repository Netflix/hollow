package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseLocalizedMetadataDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseLocalizedMetadataDelegate {

    private final int SUPPLEMENTAL_MESSAGEOrdinal;
    private final int MERCH_OVERRIDE_MESSAGEOrdinal;
    private final int POSTPLAY_OVERRIDE_MESSAGEOrdinal;
    private final int ODP_OVERRIDE_MESSAGEOrdinal;
    private final int TAGLINEOrdinal;
    private RolloutPhaseLocalizedMetadataTypeAPI typeAPI;

    public RolloutPhaseLocalizedMetadataDelegateCachedImpl(RolloutPhaseLocalizedMetadataTypeAPI typeAPI, int ordinal) {
        this.SUPPLEMENTAL_MESSAGEOrdinal = typeAPI.getSUPPLEMENTAL_MESSAGEOrdinal(ordinal);
        this.MERCH_OVERRIDE_MESSAGEOrdinal = typeAPI.getMERCH_OVERRIDE_MESSAGEOrdinal(ordinal);
        this.POSTPLAY_OVERRIDE_MESSAGEOrdinal = typeAPI.getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(ordinal);
        this.ODP_OVERRIDE_MESSAGEOrdinal = typeAPI.getODP_OVERRIDE_MESSAGEOrdinal(ordinal);
        this.TAGLINEOrdinal = typeAPI.getTAGLINEOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getSUPPLEMENTAL_MESSAGEOrdinal(int ordinal) {
        return SUPPLEMENTAL_MESSAGEOrdinal;
    }

    public int getMERCH_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return MERCH_OVERRIDE_MESSAGEOrdinal;
    }

    public int getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return POSTPLAY_OVERRIDE_MESSAGEOrdinal;
    }

    public int getODP_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return ODP_OVERRIDE_MESSAGEOrdinal;
    }

    public int getTAGLINEOrdinal(int ordinal) {
        return TAGLINEOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RolloutPhaseLocalizedMetadataTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhaseLocalizedMetadataTypeAPI) typeAPI;
    }

}