package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseCastDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseCastDelegate {

    private final RolloutPhaseCastTypeAPI typeAPI;

    public RolloutPhaseCastDelegateLookupImpl(RolloutPhaseCastTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public long getPersonId(int ordinal) {
        return typeAPI.getPersonId(ordinal);
    }

    public Long getPersonIdBoxed(int ordinal) {
        return typeAPI.getPersonIdBoxed(ordinal);
    }

    public RolloutPhaseCastTypeAPI getTypeAPI() {
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