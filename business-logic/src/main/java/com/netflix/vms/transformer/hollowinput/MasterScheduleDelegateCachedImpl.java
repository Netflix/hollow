package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MasterScheduleDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MasterScheduleDelegate {

    private final int movieTypeOrdinal;
    private final Long versionId;
    private final int scheduleIdOrdinal;
    private final int phaseTagOrdinal;
    private final Long availabilityOffset;
   private MasterScheduleTypeAPI typeAPI;

    public MasterScheduleDelegateCachedImpl(MasterScheduleTypeAPI typeAPI, int ordinal) {
        this.movieTypeOrdinal = typeAPI.getMovieTypeOrdinal(ordinal);
        this.versionId = typeAPI.getVersionIdBoxed(ordinal);
        this.scheduleIdOrdinal = typeAPI.getScheduleIdOrdinal(ordinal);
        this.phaseTagOrdinal = typeAPI.getPhaseTagOrdinal(ordinal);
        this.availabilityOffset = typeAPI.getAvailabilityOffsetBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getMovieTypeOrdinal(int ordinal) {
        return movieTypeOrdinal;
    }

    public long getVersionId(int ordinal) {
        return versionId.longValue();
    }

    public Long getVersionIdBoxed(int ordinal) {
        return versionId;
    }

    public int getScheduleIdOrdinal(int ordinal) {
        return scheduleIdOrdinal;
    }

    public int getPhaseTagOrdinal(int ordinal) {
        return phaseTagOrdinal;
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