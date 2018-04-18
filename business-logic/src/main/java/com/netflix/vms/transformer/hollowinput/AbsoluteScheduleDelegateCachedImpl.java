package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AbsoluteScheduleDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AbsoluteScheduleDelegate {

    private final Long movieId;
    private final int phaseTagOrdinal;
    private final Long startDate;
    private final Long endDate;
    private AbsoluteScheduleTypeAPI typeAPI;

    public AbsoluteScheduleDelegateCachedImpl(AbsoluteScheduleTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.phaseTagOrdinal = typeAPI.getPhaseTagOrdinal(ordinal);
        this.startDate = typeAPI.getStartDateBoxed(ordinal);
        this.endDate = typeAPI.getEndDateBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getPhaseTagOrdinal(int ordinal) {
        return phaseTagOrdinal;
    }

    public long getStartDate(int ordinal) {
        if(startDate == null)
            return Long.MIN_VALUE;
        return startDate.longValue();
    }

    public Long getStartDateBoxed(int ordinal) {
        return startDate;
    }

    public long getEndDate(int ordinal) {
        if(endDate == null)
            return Long.MIN_VALUE;
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