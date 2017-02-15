package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class OverrideScheduleDelegateLookupImpl extends HollowObjectAbstractDelegate implements OverrideScheduleDelegate {

    private final OverrideScheduleTypeAPI typeAPI;

    public OverrideScheduleDelegateLookupImpl(OverrideScheduleTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
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

    public OverrideScheduleTypeAPI getTypeAPI() {
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