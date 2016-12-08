package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class OverrideScheduleDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, OverrideScheduleDelegate {

    private final Long movieId;
    private final String phaseTag;
    private final Long availabilityOffset;
   private OverrideScheduleTypeAPI typeAPI;

    public OverrideScheduleDelegateCachedImpl(OverrideScheduleTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.phaseTag = typeAPI.getPhaseTag(ordinal);
        this.availabilityOffset = typeAPI.getAvailabilityOffsetBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
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

    public OverrideScheduleTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (OverrideScheduleTypeAPI) typeAPI;
    }

}