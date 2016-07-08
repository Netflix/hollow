package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsWindowDelegateLookupImpl extends HollowObjectAbstractDelegate implements RightsWindowDelegate {

    private final RightsWindowTypeAPI typeAPI;

    public RightsWindowDelegateLookupImpl(RightsWindowTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getStartDate(int ordinal) {
        return typeAPI.getStartDate(ordinal);
    }

    public Long getStartDateBoxed(int ordinal) {
        return typeAPI.getStartDateBoxed(ordinal);
    }

    public long getEndDate(int ordinal) {
        return typeAPI.getEndDate(ordinal);
    }

    public Long getEndDateBoxed(int ordinal) {
        return typeAPI.getEndDateBoxed(ordinal);
    }

    public int getContractIdsOrdinal(int ordinal) {
        return typeAPI.getContractIdsOrdinal(ordinal);
    }

    public RightsWindowTypeAPI getTypeAPI() {
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