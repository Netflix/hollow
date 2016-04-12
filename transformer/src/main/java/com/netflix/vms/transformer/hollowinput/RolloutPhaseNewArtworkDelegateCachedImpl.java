package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhaseNewArtworkDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseNewArtworkDelegate {

    private final int sourceFileIdsOrdinal;
   private RolloutPhaseNewArtworkTypeAPI typeAPI;

    public RolloutPhaseNewArtworkDelegateCachedImpl(RolloutPhaseNewArtworkTypeAPI typeAPI, int ordinal) {
        this.sourceFileIdsOrdinal = typeAPI.getSourceFileIdsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getSourceFileIdsOrdinal(int ordinal) {
        return sourceFileIdsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RolloutPhaseNewArtworkTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhaseNewArtworkTypeAPI) typeAPI;
    }

}