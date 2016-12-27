package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class MasterScheduleDelegateLookupImpl extends HollowObjectAbstractDelegate implements MasterScheduleDelegate {

    private final MasterScheduleTypeAPI typeAPI;

    public MasterScheduleDelegateLookupImpl(MasterScheduleTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getMovieTypeOrdinal(int ordinal) {
        return typeAPI.getMovieTypeOrdinal(ordinal);
    }

    public long getVersionId(int ordinal) {
        return typeAPI.getVersionId(ordinal);
    }

    public Long getVersionIdBoxed(int ordinal) {
        return typeAPI.getVersionIdBoxed(ordinal);
    }

    public int getScheduleIdOrdinal(int ordinal) {
        return typeAPI.getScheduleIdOrdinal(ordinal);
    }

    public int getPhaseTagOrdinal(int ordinal) {
        return typeAPI.getPhaseTagOrdinal(ordinal);
    }

    public long getAvailabilityOffset(int ordinal) {
        return typeAPI.getAvailabilityOffset(ordinal);
    }

    public Long getAvailabilityOffsetBoxed(int ordinal) {
        return typeAPI.getAvailabilityOffsetBoxed(ordinal);
    }

    public MasterScheduleTypeAPI getTypeAPI() {
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