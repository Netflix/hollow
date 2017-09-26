package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ExplicitDateDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ExplicitDateDelegate {

    private final Integer monthOfYear;
    private final Integer year;
    private final Integer dayOfMonth;
    private ExplicitDateTypeAPI typeAPI;

    public ExplicitDateDelegateCachedImpl(ExplicitDateTypeAPI typeAPI, int ordinal) {
        this.monthOfYear = typeAPI.getMonthOfYearBoxed(ordinal);
        this.year = typeAPI.getYearBoxed(ordinal);
        this.dayOfMonth = typeAPI.getDayOfMonthBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getMonthOfYear(int ordinal) {
        if(monthOfYear == null)
            return Integer.MIN_VALUE;
        return monthOfYear.intValue();
    }

    public Integer getMonthOfYearBoxed(int ordinal) {
        return monthOfYear;
    }

    public int getYear(int ordinal) {
        if(year == null)
            return Integer.MIN_VALUE;
        return year.intValue();
    }

    public Integer getYearBoxed(int ordinal) {
        return year;
    }

    public int getDayOfMonth(int ordinal) {
        if(dayOfMonth == null)
            return Integer.MIN_VALUE;
        return dayOfMonth.intValue();
    }

    public Integer getDayOfMonthBoxed(int ordinal) {
        return dayOfMonth;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ExplicitDateTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ExplicitDateTypeAPI) typeAPI;
    }

}