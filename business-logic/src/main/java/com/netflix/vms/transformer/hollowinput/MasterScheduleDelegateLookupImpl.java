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

    public String getMovieType(int ordinal) {
        return typeAPI.getMovieType(ordinal);
    }

    public boolean isMovieTypeEqual(int ordinal, String testValue) {
        return typeAPI.isMovieTypeEqual(ordinal, testValue);
    }

    public long getVersionId(int ordinal) {
        return typeAPI.getVersionId(ordinal);
    }

    public Long getVersionIdBoxed(int ordinal) {
        return typeAPI.getVersionIdBoxed(ordinal);
    }

    public String getScheduleId(int ordinal) {
        return typeAPI.getScheduleId(ordinal);
    }

    public boolean isScheduleIdEqual(int ordinal, String testValue) {
        return typeAPI.isScheduleIdEqual(ordinal, testValue);
    }

    public String getPhaseTag(int ordinal) {
        return typeAPI.getPhaseTag(ordinal);
    }

    public boolean isPhaseTagEqual(int ordinal, String testValue) {
        return typeAPI.isPhaseTagEqual(ordinal, testValue);
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