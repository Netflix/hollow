package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhasesElementsArtworkDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhasesElementsArtworkDelegate {

    private final Long imageId;
   private RolloutPhasesElementsArtworkTypeAPI typeAPI;

    public RolloutPhasesElementsArtworkDelegateCachedImpl(RolloutPhasesElementsArtworkTypeAPI typeAPI, int ordinal) {
        this.imageId = typeAPI.getImageIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getImageId(int ordinal) {
        return imageId.longValue();
    }

    public Long getImageIdBoxed(int ordinal) {
        return imageId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RolloutPhasesElementsArtworkTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhasesElementsArtworkTypeAPI) typeAPI;
    }

}