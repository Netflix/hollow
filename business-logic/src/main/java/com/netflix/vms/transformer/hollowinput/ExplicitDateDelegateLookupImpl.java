package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ExplicitDateDelegateLookupImpl extends HollowObjectAbstractDelegate implements ExplicitDateDelegate {

    private final ExplicitDateTypeAPI typeAPI;

    public ExplicitDateDelegateLookupImpl(ExplicitDateTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getMonthOfYear(int ordinal) {
        return typeAPI.getMonthOfYear(ordinal);
    }

    public Integer getMonthOfYearBoxed(int ordinal) {
        return typeAPI.getMonthOfYearBoxed(ordinal);
    }

    public int getYear(int ordinal) {
        return typeAPI.getYear(ordinal);
    }

    public Integer getYearBoxed(int ordinal) {
        return typeAPI.getYearBoxed(ordinal);
    }

    public int getDayOfMonth(int ordinal) {
        return typeAPI.getDayOfMonth(ordinal);
    }

    public Integer getDayOfMonthBoxed(int ordinal) {
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