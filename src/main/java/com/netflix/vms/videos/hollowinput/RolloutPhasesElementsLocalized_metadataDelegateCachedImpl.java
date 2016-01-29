package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhasesElementsLocalized_metadataDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhasesElementsLocalized_metadataDelegate {

    private final int SUPPLEMENTAL_MESSAGEOrdinal;
    private final int TAGLINEOrdinal;
   private RolloutPhasesElementsLocalized_metadataTypeAPI typeAPI;

    public RolloutPhasesElementsLocalized_metadataDelegateCachedImpl(RolloutPhasesElementsLocalized_metadataTypeAPI typeAPI, int ordinal) {
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

    public RolloutPhasesElementsLocalized_metadataTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhasesElementsLocalized_metadataTypeAPI) typeAPI;
    }

}