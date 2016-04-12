package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ExplicitDateDelegateLookupImpl extends HollowObjectAbstractDelegate implements ExplicitDateDelegate {

    private final ExplicitDateTypeAPI typeAPI;

    public ExplicitDateDelegateLookupImpl(ExplicitDateTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMonthOfYear(int ordinal) {
        return typeAPI.getMonthOfYear(ordinal);
    }

    public Long getMonthOfYearBoxed(int ordinal) {
        return typeAPI.getMonthOfYearBoxed(ordinal);
    }

    public long getYear(int ordinal) {
        return typeAPI.getYear(ordinal);
    }

    public Long getYearBoxed(int ordinal) {
        return typeAPI.getYearBoxed(ordinal);
    }

    public long getDayOfMonth(int ordinal) {
        return typeAPI.getDayOfMonth(ordinal);
    }

    public Long getDayOfMonthBoxed(int ordinal) {
        return typeAPI.getDayOfMonthBoxed(ordinal);
    }

    public ExplicitDateTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}