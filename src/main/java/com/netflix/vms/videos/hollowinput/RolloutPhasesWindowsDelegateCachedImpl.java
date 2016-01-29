package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhasesWindowsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhasesWindowsDelegate {

    private final Long endDate;
    private final Long startDate;
   private RolloutPhasesWindowsTypeAPI typeAPI;

    public RolloutPhasesWindowsDelegateCachedImpl(RolloutPhasesWindowsTypeAPI typeAPI, int ordinal) {
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

    public RolloutPhasesWindowsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhasesWindowsTypeAPI) typeAPI;
    }

}