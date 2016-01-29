package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesWindowsDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhasesWindowsDelegate {

    private final RolloutPhasesWindowsTypeAPI typeAPI;

    public RolloutPhasesWindowsDelegateLookupImpl(RolloutPhasesWindowsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getEndDate(int ordinal) {
        return typeAPI.getEndDate(ordinal);
    }

    public Long getEndDateBoxed(int ordinal) {
        return typeAPI.getEndDateBoxed(ordinal);
    }

    public long getStartDate(int ordinal) {
        return typeAPI.getStartDate(ordinal);
    }

    public Long getStartDateBoxed(int ordinal) {
        return typeAPI.getStartDateBoxed(ordinal);
    }

    public RolloutPhasesWindowsTypeAPI getTypeAPI() {
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