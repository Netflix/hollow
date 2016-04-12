package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhaseImageIdDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseImageIdDelegate {

    private final Long imageId;
   private RolloutPhaseImageIdTypeAPI typeAPI;

    public RolloutPhaseImageIdDelegateCachedImpl(RolloutPhaseImageIdTypeAPI typeAPI, int ordinal) {
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

    public RolloutPhaseImageIdTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhaseImageIdTypeAPI) typeAPI;
    }

}