package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieDelegate {

    private final MovieTypeAPI typeAPI;

    public MovieDelegateLookupImpl(MovieTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return typeAPI.getId(ordinal);
    }

    public Long getIdBoxed(int ordinal) {
        return typeAPI.getIdBoxed(ordinal);
    }

    public int getTitleOrdinal(int ordinal) {
        return typeAPI.getTitleOrdinal(ordinal);
    }

    public int getYear(int ordinal) {
        return typeAPI.getYear(ordinal);
    }

    public Integer getYearBoxed(int ordinal) {
        return typeAPI.getYearBoxed(ordinal);
    }

    public MovieTypeAPI getTypeAPI() {
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