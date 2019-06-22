package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

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

    public boolean getOnHold(int ordinal) {
        return typeAPI.getOnHold(ordinal);
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        return typeAPI.getOnHoldBoxed(ordinal);
    }

    public int getContractIdsExtOrdinal(int ordinal) {
        return typeAPI.getContractIdsExtOrdinal(ordinal);
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