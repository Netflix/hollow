package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ExplicitDateTypeAPI extends HollowObjectTypeAPI {

    private final ExplicitDateDelegateLookupImpl delegateLookupImpl;

    ExplicitDateTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "monthOfYear",
            "year",
            "dayOfMonth"
        });
        this.delegateLookupImpl = new ExplicitDateDelegateLookupImpl(this);
    }

    public int getMonthOfYear(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleInt("ExplicitDate", ordinal, "monthOfYear");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getMonthOfYearBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleInt("ExplicitDate", ordinal, "monthOfYear");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getYear(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("ExplicitDate", ordinal, "year");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getYearBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("ExplicitDate", ordinal, "year");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getDayOfMonth(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleInt("ExplicitDate", ordinal, "dayOfMonth");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
    }

    public Integer getDayOfMonthBoxed(int ordinal) {
        int i;
        if(fieldIndex[2] == -1) {
            i = missingDataHandler().handleInt("ExplicitDate", ordinal, "dayOfMonth");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public ExplicitDateDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}