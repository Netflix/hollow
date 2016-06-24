package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class RolloutPhaseLocalizedMetadataDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseLocalizedMetadataDelegate {

    private final int SUPPLEMENTAL_MESSAGEOrdinal;
    private final int TAGLINEOrdinal;
   private RolloutPhaseLocalizedMetadataTypeAPI typeAPI;

    public RolloutPhaseLocalizedMetadataDelegateCachedImpl(RolloutPhaseLocalizedMetadataTypeAPI typeAPI, int ordinal) {
        this.SUPPLEMENTAL_MESSAGEOrdinal = typeAPI.getSUPPLEMENTAL_MESSAGEOrdinal(ordinal);
        this.TAGLINEOrdinal = typeAPI.getTAGLINEOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getSUPPLEMENTAL_MESSAGEOrdinal(int ordinal) {
        return SUPPLEMENTAL_MESSAGEOrdinal;
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