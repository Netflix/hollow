package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseNewArtworkDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseNewArtworkDelegate {

    private final RolloutPhaseNewArtworkTypeAPI typeAPI;

    public RolloutPhaseNewArtworkDelegateLookupImpl(RolloutPhaseNewArtworkTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getSourceFileIdsOrdinal(int ordinal) {
        return typeAPI.getSourceFileIdsOrdinal(ordinal);
    }

    public RolloutPhaseNewArtworkTypeAPI getTypeAPI() {
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