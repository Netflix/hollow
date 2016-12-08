package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MasterScheduleTypeAPI extends HollowObjectTypeAPI {

    private final MasterScheduleDelegateLookupImpl delegateLookupImpl;

    MasterScheduleTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieType",
            "versionId",
            "scheduleId",
            "phaseTag",
            "availabilityOffset"
        });
        this.delegateLookupImpl = new MasterScheduleDelegateLookupImpl(this);
    }

    public String getMovieType(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleString("MasterSchedule", ordinal, "movieType");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[0]);
    }

    public boolean isMovieTypeEqual(int ordinal, String testValue) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleStringEquals("MasterSchedule", ordinal, "movieType", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[0], testValue);
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



    public String getScheduleId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleString("MasterSchedule", ordinal, "scheduleId");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[2]);
    }

    public boolean isScheduleIdEqual(int ordinal, String testValue) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleStringEquals("MasterSchedule", ordinal, "scheduleId", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[2], testValue);
    }

    public String getPhaseTag(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleString("MasterSchedule", ordinal, "phaseTag");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[3]);
    }

    public boolean isPhaseTagEqual(int ordinal, String testValue) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleStringEquals("MasterSchedule", ordinal, "phaseTag", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[3], testValue);
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