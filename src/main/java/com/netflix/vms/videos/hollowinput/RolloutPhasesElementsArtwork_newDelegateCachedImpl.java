package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhasesElementsArtwork_newDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhasesElementsArtwork_newDelegate {

    private final int sourceFileIdsOrdinal;
   private RolloutPhasesElementsArtwork_newTypeAPI typeAPI;

    public RolloutPhasesElementsArtwork_newDelegateCachedImpl(RolloutPhasesElementsArtwork_newTypeAPI typeAPI, int ordinal) {
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

    public RolloutPhasesElementsArtwork_newTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhasesElementsArtwork_newTypeAPI) typeAPI;
    }

}