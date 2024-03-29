package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieDelegate {

    private final Long id;
    private final String title;
    private final int titleOrdinal;
    private final Integer year;
    private MovieTypeAPI typeAPI;

    public MovieDelegateCachedImpl(MovieTypeAPI typeAPI, int ordinal) {
        this.id = typeAPI.getIdBoxed(ordinal);
        this.titleOrdinal = typeAPI.getTitleOrdinal(ordinal);
        int titleTempOrdinal = titleOrdinal;
        this.title = titleTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(titleTempOrdinal);
        this.year = typeAPI.getYearBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        if(id == null)
            return Long.MIN_VALUE;
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
    }

    public String getTitle(int ordinal) {
        return title;
    }

    public boolean isTitleEqual(int ordinal, String testValue) {
        if(testValue == null)
            return title == null;
        return testValue.equals(title);
    }

    public int getTitleOrdinal(int ordinal) {
        return titleOrdinal;
    }

    public int getYear(int ordinal) {
        if(year == null)
            return Integer.MIN_VALUE;
        return year.intValue();
    }

    public Integer getYearBoxed(int ordinal) {
        return year;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public MovieTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieTypeAPI) typeAPI;
    }

}