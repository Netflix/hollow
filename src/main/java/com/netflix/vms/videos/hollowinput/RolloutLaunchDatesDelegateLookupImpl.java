package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutLaunchDatesDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutLaunchDatesDelegate {

    private final RolloutLaunchDatesTypeAPI typeAPI;

    public RolloutLaunchDatesDelegateLookupImpl(RolloutLaunchDatesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getValue(int ordinal) {
        return typeAPI.getValue(ordinal);
    }

    public Long getValueBoxed(int ordinal) {
        return typeAPI.getValueBoxed(ordinal);
    }

    public RolloutLaunchDatesTypeAPI getTypeAPI() {
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