package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class ExplicitDateDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ExplicitDateDelegate {

    private final Long monthOfYear;
    private final Long year;
    private final Long dayOfMonth;
   private ExplicitDateTypeAPI typeAPI;

    public ExplicitDateDelegateCachedImpl(ExplicitDateTypeAPI typeAPI, int ordinal) {
        this.monthOfYear = typeAPI.getMonthOfYearBoxed(ordinal);
        this.year = typeAPI.getYearBoxed(ordinal);
        this.dayOfMonth = typeAPI.getDayOfMonthBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMonthOfYear(int ordinal) {
        return monthOfYear.longValue();
    }

    public Long getMonthOfYearBoxed(int ordinal) {
        return monthOfYear;
    }

    public long getYear(int ordinal) {
        return year.longValue();
    }

    public Long getYearBoxed(int ordinal) {
        return year;
    }

    public long getDayOfMonth(int ordinal) {
        return dayOfMonth.longValue();
    }

    public Long getDayOfMonthBoxed(int ordinal) {
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