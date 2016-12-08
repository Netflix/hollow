package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MasterScheduleDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MasterScheduleDelegate {

    private final String movieType;
    private final Long versionId;
    private final String scheduleId;
    private final String phaseTag;
    private final Long availabilityOffset;
   private MasterScheduleTypeAPI typeAPI;

    public MasterScheduleDelegateCachedImpl(MasterScheduleTypeAPI typeAPI, int ordinal) {
        this.movieType = typeAPI.getMovieType(ordinal);
        this.versionId = typeAPI.getVersionIdBoxed(ordinal);
        this.scheduleId = typeAPI.getScheduleId(ordinal);
        this.phaseTag = typeAPI.getPhaseTag(ordinal);
        this.availabilityOffset = typeAPI.getAvailabilityOffsetBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getMovieType(int ordinal) {
        return movieType;
    }

    public boolean isMovieTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return movieType == null;
        return testValue.equals(movieType);
    }

    public long getVersionId(int ordinal) {
        return versionId.longValue();
    }

    public Long getVersionIdBoxed(int ordinal) {
        return versionId;
    }

    public String getScheduleId(int ordinal) {
        return scheduleId;
    }

    public boolean isScheduleIdEqual(int ordinal, String testValue) {
        if(testValue == null)
            return scheduleId == null;
        return testValue.equals(scheduleId);
    }

    public String getPhaseTag(int ordinal) {
        return phaseTag;
    }

    public boolean isPhaseTagEqual(int ordinal, String testValue) {
        if(testValue == null)
            return phaseTag == null;
        return testValue.equals(phaseTag);
    }

    public long getAvailabilityOffset(int ordinal) {
        return availabilityOffset.longValue();
    }

    public Long getAvailabilityOffsetBoxed(int ordinal) {
        return availabilityOffset;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public MasterScheduleTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MasterScheduleTypeAPI) typeAPI;
    }

}