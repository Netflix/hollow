package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ReleaseDateDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ReleaseDateDelegate {

    private final int releaseDateTypeOrdinal;
    private final int distributorNameOrdinal;
    private final Integer month;
    private final Integer year;
    private final Integer day;
    private final int bcp47codeOrdinal;
    private ReleaseDateTypeAPI typeAPI;

    public ReleaseDateDelegateCachedImpl(ReleaseDateTypeAPI typeAPI, int ordinal) {
        this.releaseDateTypeOrdinal = typeAPI.getReleaseDateTypeOrdinal(ordinal);
        this.distributorNameOrdinal = typeAPI.getDistributorNameOrdinal(ordinal);
        this.month = typeAPI.getMonthBoxed(ordinal);
        this.year = typeAPI.getYearBoxed(ordinal);
        this.day = typeAPI.getDayBoxed(ordinal);
        this.bcp47codeOrdinal = typeAPI.getBcp47codeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getReleaseDateTypeOrdinal(int ordinal) {
        return releaseDateTypeOrdinal;
    }

    public int getDistributorNameOrdinal(int ordinal) {
        return distributorNameOrdinal;
    }

    public int getMonth(int ordinal) {
        if(month == null)
            return Integer.MIN_VALUE;
        return month.intValue();
    }

    public Integer getMonthBoxed(int ordinal) {
        return month;
    }

    public int getYear(int ordinal) {
        if(year == null)
            return Integer.MIN_VALUE;
        return year.intValue();
    }

    public Integer getYearBoxed(int ordinal) {
        return year;
    }

    public int getDay(int ordinal) {
        if(day == null)
            return Integer.MIN_VALUE;
        return day.intValue();
    }

    public Integer getDayBoxed(int ordinal) {
        return day;
    }

    public int getBcp47codeOrdinal(int ordinal) {
        return bcp47codeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ReleaseDateTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ReleaseDateTypeAPI) typeAPI;
    }

}