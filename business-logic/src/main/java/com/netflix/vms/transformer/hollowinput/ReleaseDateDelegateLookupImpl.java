package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ReleaseDateDelegateLookupImpl extends HollowObjectAbstractDelegate implements ReleaseDateDelegate {

    private final ReleaseDateTypeAPI typeAPI;

    public ReleaseDateDelegateLookupImpl(ReleaseDateTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getReleaseDateTypeOrdinal(int ordinal) {
        return typeAPI.getReleaseDateTypeOrdinal(ordinal);
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