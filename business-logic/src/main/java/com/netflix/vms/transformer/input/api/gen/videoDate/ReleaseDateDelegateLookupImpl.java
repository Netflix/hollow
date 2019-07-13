package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ReleaseDateDelegateLookupImpl extends HollowObjectAbstractDelegate implements ReleaseDateDelegate {

    private final ReleaseDateTypeAPI typeAPI;

    public ReleaseDateDelegateLookupImpl(ReleaseDateTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getReleaseDateType(int ordinal) {
        ordinal = typeAPI.getReleaseDateTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isReleaseDateTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getReleaseDateTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getReleaseDateTypeOrdinal(int ordinal) {
        return typeAPI.getReleaseDateTypeOrdinal(ordinal);
    }

    public String getDistributorName(int ordinal) {
        ordinal = typeAPI.getDistributorNameOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isDistributorNameEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getDistributorNameOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getDistributorNameOrdinal(int ordinal) {
        return typeAPI.getDistributorNameOrdinal(ordinal);
    }

    public int getMonth(int ordinal) {
        return typeAPI.getMonth(ordinal);
    }

    public Integer getMonthBoxed(int ordinal) {
        return typeAPI.getMonthBoxed(ordinal);
    }

    public int getYear(int ordinal) {
        return typeAPI.getYear(ordinal);
    }

    public Integer getYearBoxed(int ordinal) {
        return typeAPI.getYearBoxed(ordinal);
    }

    public int getDay(int ordinal) {
        return typeAPI.getDay(ordinal);
    }

    public Integer getDayBoxed(int ordinal) {
        return typeAPI.getDayBoxed(ordinal);
    }

    public String getBcp47code(int ordinal) {
        ordinal = typeAPI.getBcp47codeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isBcp47codeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getBcp47codeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getBcp47codeOrdinal(int ordinal) {
        return typeAPI.getBcp47codeOrdinal(ordinal);
    }

    public ReleaseDateTypeAPI getTypeAPI() {
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