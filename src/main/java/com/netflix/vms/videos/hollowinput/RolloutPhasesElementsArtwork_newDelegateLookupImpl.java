package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesElementsArtwork_newDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhasesElementsArtwork_newDelegate {

    private final RolloutPhasesElementsArtwork_newTypeAPI typeAPI;

    public RolloutPhasesElementsArtwork_newDelegateLookupImpl(RolloutPhasesElementsArtwork_newTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getSourceFileIdsOrdinal(int ordinal) {
        return typeAPI.getSourceFileIdsOrdinal(ordinal);
    }

    public RolloutPhasesElementsArtwork_newTypeAPI getTypeAPI() {
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