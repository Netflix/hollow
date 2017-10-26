package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class AbsoluteScheduleTypeAPI extends HollowObjectTypeAPI {

    private final AbsoluteScheduleDelegateLookupImpl delegateLookupImpl;

    public AbsoluteScheduleTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "phaseTag",
            "startDate",
            "endDate"
        });
        this.delegateLookupImpl = new AbsoluteScheduleDelegateLookupImpl(this);
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("AbsoluteSchedule", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("AbsoluteSchedule", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getPhaseTagOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("AbsoluteSchedule", ordinal, "phaseTag");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getPhaseTagTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getStartDate(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("AbsoluteSchedule", ordinal, "startDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getStartDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("AbsoluteSchedule", ordinal, "startDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getEndDate(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("AbsoluteSchedule", ordinal, "endDate");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getEndDateBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("AbsoluteSchedule", ordinal, "endDate");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public AbsoluteScheduleDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}