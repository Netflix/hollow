package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MasterScheduleTypeAPI extends HollowObjectTypeAPI {

    private final MasterScheduleDelegateLookupImpl delegateLookupImpl;

    public MasterScheduleTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieType",
            "versionId",
            "scheduleId",
            "phaseTag",
            "availabilityOffset"
        });
        this.delegateLookupImpl = new MasterScheduleDelegateLookupImpl(this);
    }

    public int getMovieTypeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("MasterSchedule", ordinal, "movieType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getMovieTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getVersionId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("MasterSchedule", ordinal, "versionId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getVersionIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("MasterSchedule", ordinal, "versionId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getScheduleIdOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("MasterSchedule", ordinal, "scheduleId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getScheduleIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getPhaseTagOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("MasterSchedule", ordinal, "phaseTag");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getPhaseTagTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getAvailabilityOffset(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("MasterSchedule", ordinal, "availabilityOffset");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getAvailabilityOffsetBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("MasterSchedule", ordinal, "availabilityOffset");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public MasterScheduleDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}