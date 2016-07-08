package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseArtworkDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseArtworkDelegate {

    private final RolloutPhaseArtworkTypeAPI typeAPI;

    public RolloutPhaseArtworkDelegateLookupImpl(RolloutPhaseArtworkTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getSourceFileIdsOrdinal(int ordinal) {
        return typeAPI.getSourceFileIdsOrdinal(ordinal);
    }

    public RolloutPhaseArtworkTypeAPI getTypeAPI() {
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