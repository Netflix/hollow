package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhaseWindowDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseWindowDelegate {

    private final Long endDate;
    private final Long startDate;
   private RolloutPhaseWindowTypeAPI typeAPI;

    public RolloutPhaseWindowDelegateCachedImpl(RolloutPhaseWindowTypeAPI typeAPI, int ordinal) {
        this.endDate = typeAPI.getEndDateBoxed(ordinal);
        this.startDate = typeAPI.getStartDateBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getEndDate(int ordinal) {
        return endDate.longValue();
    }

    public Long getEndDateBoxed(int ordinal) {
        return endDate;
    }

    public long getStartDate(int ordinal) {
        return startDate.longValue();
    }

    public Long getStartDateBoxed(int ordinal) {
        return startDate;
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