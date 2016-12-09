package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class PhaseTagDelegateLookupImpl extends HollowObjectAbstractDelegate implements PhaseTagDelegate {

    private final PhaseTagTypeAPI typeAPI;

    public PhaseTagDelegateLookupImpl(PhaseTagTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getPhaseTagOrdinal(int ordinal) {
        return typeAPI.getPhaseTagOrdinal(ordinal);
    }

    public int getScheduleIdOrdinal(int ordinal) {
        return typeAPI.getScheduleIdOrdinal(ordinal);
    }

    public PhaseTagTypeAPI getTypeAPI() {
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