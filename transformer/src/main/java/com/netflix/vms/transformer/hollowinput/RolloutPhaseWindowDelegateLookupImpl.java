package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseWindowDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseWindowDelegate {

    private final RolloutPhaseWindowTypeAPI typeAPI;

    public RolloutPhaseWindowDelegateLookupImpl(RolloutPhaseWindowTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getEndDateOrdinal(int ordinal) {
        return typeAPI.getEndDateOrdinal(ordinal);
    }

    public int getStartDateOrdinal(int ordinal) {
        return typeAPI.getStartDateOrdinal(ordinal);
    }

    public RolloutPhaseWindowTypeAPI getTypeAPI() {
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