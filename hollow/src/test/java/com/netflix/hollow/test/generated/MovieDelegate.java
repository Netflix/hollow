package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieDelegate extends HollowObjectDelegate {

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public int getTitleOrdinal(int ordinal);

    public int getYear(int ordinal);

    public Integer getYearBoxed(int ordinal);

    public MovieTypeAPI getTypeAPI();

}