package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ExplicitDateDelegate extends HollowObjectDelegate {

    public int getMonthOfYear(int ordinal);

    public Integer getMonthOfYearBoxed(int ordinal);

    public int getYear(int ordinal);

    public Integer getYearBoxed(int ordinal);

    public int getDayOfMonth(int ordinal);

    public Integer getDayOfMonthBoxed(int ordinal);

    public ExplicitDateTypeAPI getTypeAPI();

}