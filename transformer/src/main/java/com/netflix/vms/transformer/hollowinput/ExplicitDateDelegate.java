package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ExplicitDateDelegate extends HollowObjectDelegate {

    public long getMonthOfYear(int ordinal);

    public Long getMonthOfYearBoxed(int ordinal);

    public long getYear(int ordinal);

    public Long getYearBoxed(int ordinal);

    public long getDayOfMonth(int ordinal);

    public Long getDayOfMonthBoxed(int ordinal);

    public ExplicitDateTypeAPI getTypeAPI();

}