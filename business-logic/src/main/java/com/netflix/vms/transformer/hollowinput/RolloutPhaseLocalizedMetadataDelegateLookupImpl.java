package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseLocalizedMetadataDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseLocalizedMetadataDelegate {

    private final RolloutPhaseLocalizedMetadataTypeAPI typeAPI;

    public RolloutPhaseLocalizedMetadataDelegateLookupImpl(RolloutPhaseLocalizedMetadataTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getSUPPLEMENTAL_MESSAGEOrdinal(int ordinal) {
        return typeAPI.getSUPPLEMENTAL_MESSAGEOrdinal(ordinal);
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