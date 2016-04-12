package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseImageIdDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseImageIdDelegate {

    private final RolloutPhaseImageIdTypeAPI typeAPI;

    public RolloutPhaseImageIdDelegateLookupImpl(RolloutPhaseImageIdTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getImageId(int ordinal) {
        return typeAPI.getImageId(ordinal);
    }

    public Long getImageIdBoxed(int ordinal) {
        return typeAPI.getImageIdBoxed(ordinal);
    }

    public RolloutPhaseImageIdTypeAPI getTypeAPI() {
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