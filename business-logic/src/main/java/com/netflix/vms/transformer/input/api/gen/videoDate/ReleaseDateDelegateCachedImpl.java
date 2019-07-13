package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ReleaseDateDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ReleaseDateDelegate {

    private final String releaseDateType;
    private final int releaseDateTypeOrdinal;
    private final String distributorName;
    private final int distributorNameOrdinal;
    private final Integer month;
    private final Integer year;
    private final Integer day;
    private final String bcp47code;
    private final int bcp47codeOrdinal;
    private ReleaseDateTypeAPI typeAPI;

    public ReleaseDateDelegateCachedImpl(ReleaseDateTypeAPI typeAPI, int ordinal) {
        this.releaseDateTypeOrdinal = typeAPI.getReleaseDateTypeOrdinal(ordinal);
        int releaseDateTypeTempOrdinal = releaseDateTypeOrdinal;
        this.releaseDateType = releaseDateTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(releaseDateTypeTempOrdinal);
        this.distributorNameOrdinal = typeAPI.getDistributorNameOrdinal(ordinal);
        int distributorNameTempOrdinal = distributorNameOrdinal;
        this.distributorName = distributorNameTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(distributorNameTempOrdinal);
        this.month = typeAPI.getMonthBoxed(ordinal);
        this.year = typeAPI.getYearBoxed(ordinal);
        this.day = typeAPI.getDayBoxed(ordinal);
        this.bcp47codeOrdinal = typeAPI.getBcp47codeOrdinal(ordinal);
        int bcp47codeTempOrdinal = bcp47codeOrdinal;
        this.bcp47code = bcp47codeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(bcp47codeTempOrdinal);
        this.typeAPI = typeAPI;
    }

    public String getReleaseDateType(int ordinal) {
        return releaseDateType;
    }

    public boolean isReleaseDateTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return releaseDateType == null;
        return testValue.equals(releaseDateType);
    }

    public int getReleaseDateTypeOrdinal(int ordinal) {
        return releaseDateTypeOrdinal;
    }

    public String getDistributorName(int ordinal) {
        return distributorName;
    }

    public boolean isDistributorNameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return distributorName == null;
        return testValue.equals(distributorName);
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

    public String getBcp47code(int ordinal) {
        return bcp47code;
    }

    public boolean isBcp47codeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return bcp47code == null;
        return testValue.equals(bcp47code);
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