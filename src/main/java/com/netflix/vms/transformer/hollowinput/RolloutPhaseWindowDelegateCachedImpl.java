package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhaseWindowDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseWindowDelegate {

    private final int endDateOrdinal;
    private final int startDateOrdinal;
   private RolloutPhaseWindowTypeAPI typeAPI;

    public RolloutPhaseWindowDelegateCachedImpl(RolloutPhaseWindowTypeAPI typeAPI, int ordinal) {
        this.endDateOrdinal = typeAPI.getEndDateOrdinal(ordinal);
        this.startDateOrdinal = typeAPI.getStartDateOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getEndDateOrdinal(int ordinal) {
        return endDateOrdinal;
    }

    public int getStartDateOrdinal(int ordinal) {
        return startDateOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RolloutPhaseWindowTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhaseWindowTypeAPI) typeAPI;
    }

}