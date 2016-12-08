package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class AbsoluteScheduleDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AbsoluteScheduleDelegate {

    private final Long movieId;
    private final String phaseTag;
    private final Long startDate;
    private final Long endDate;
   private AbsoluteScheduleTypeAPI typeAPI;

    public AbsoluteScheduleDelegateCachedImpl(AbsoluteScheduleTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.phaseTag = typeAPI.getPhaseTag(ordinal);
        this.startDate = typeAPI.getStartDateBoxed(ordinal);
        this.endDate = typeAPI.getEndDateBoxed(ordinal);
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

    public long getStartDate(int ordinal) {
        return startDate.longValue();
    }

    public Long getStartDateBoxed(int ordinal) {
        return startDate;
    }

    public long getEndDate(int ordinal) {
        return endDate.longValue();
    }

    public Long getEndDateBoxed(int ordinal) {
        return endDate;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AbsoluteScheduleTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AbsoluteScheduleTypeAPI) typeAPI;
    }

}