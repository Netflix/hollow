package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesElementsLocalized_metadataDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhasesElementsLocalized_metadataDelegate {

    private final RolloutPhasesElementsLocalized_metadataTypeAPI typeAPI;

    public RolloutPhasesElementsLocalized_metadataDelegateLookupImpl(RolloutPhasesElementsLocalized_metadataTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getSUPPLEMENTAL_MESSAGEOrdinal(int ordinal) {
        return typeAPI.getSUPPLEMENTAL_MESSAGEOrdinal(ordinal);
    }

    public int getTAGLINEOrdinal(int ordinal) {
        return typeAPI.getTAGLINEOrdinal(ordinal);
    }

    public RolloutPhasesElementsLocalized_metadataTypeAPI getTypeAPI() {
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