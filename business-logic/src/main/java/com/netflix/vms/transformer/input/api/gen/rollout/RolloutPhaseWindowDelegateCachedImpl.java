package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
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
        if(endDate == null)
            return Long.MIN_VALUE;
        return endDate.longValue();
    }

    public Long getEndDateBoxed(int ordinal) {
        return endDate;
    }

    public long getStartDate(int ordinal) {
        if(startDate == null)
            return Long.MIN_VALUE;
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