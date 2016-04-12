package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ExplicitDateTypeAPI extends HollowObjectTypeAPI {

    private final ExplicitDateDelegateLookupImpl delegateLookupImpl;

    ExplicitDateTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "monthOfYear",
            "year",
            "dayOfMonth"
        });
        this.delegateLookupImpl = new ExplicitDateDelegateLookupImpl(this);
    }

    public long getMonthOfYear(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("ExplicitDate", ordinal, "monthOfYear");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMonthOfYearBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("ExplicitDate", ordinal, "monthOfYear");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getYear(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("ExplicitDate", ordinal, "year");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getYearBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("ExplicitDate", ordinal, "year");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getDayOfMonth(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("ExplicitDate", ordinal, "dayOfMonth");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getDayOfMonthBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("ExplicitDate", ordinal, "dayOfMonth");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public ExplicitDateDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}