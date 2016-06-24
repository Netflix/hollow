package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ReleaseDateDelegate extends HollowObjectDelegate {

    public int getReleaseDateTypeOrdinal(int ordinal);

    public int getDistributorNameOrdinal(int ordinal);

    public int getMonth(int ordinal);

    public Integer getMonthBoxed(int ordinal);

    public int getYear(int ordinal);

    public Integer getYearBoxed(int ordinal);

    public int getDay(int ordinal);

    public Integer getDayBoxed(int ordinal);

    public int getBcp47codeOrdinal(int ordinal);

    public ReleaseDateTypeAPI getTypeAPI();

}