package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesElementsArtworkDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhasesElementsArtworkDelegate {

    private final RolloutPhasesElementsArtworkTypeAPI typeAPI;

    public RolloutPhasesElementsArtworkDelegateLookupImpl(RolloutPhasesElementsArtworkTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getImageId(int ordinal) {
        return typeAPI.getImageId(ordinal);
    }

    public Long getImageIdBoxed(int ordinal) {
        return typeAPI.getImageIdBoxed(ordinal);
    }

    public RolloutPhasesElementsArtworkTypeAPI getTypeAPI() {
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