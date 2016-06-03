package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ExplicitDateHollow extends HollowObject {

    public ExplicitDateHollow(ExplicitDateDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int _getMonthOfYear() {
        return delegate().getMonthOfYear(ordinal);
    }

    public Integer _getMonthOfYearBoxed() {
        return delegate().getMonthOfYearBoxed(ordinal);
    }

    public int _getYear() {
        return delegate().getYear(ordinal);
    }

    public Integer _getYearBoxed() {
        return delegate().getYearBoxed(ordinal);
    }

    public int _getDayOfMonth() {
        return delegate().getDayOfMonth(ordinal);
    }

    public Integer _getDayOfMonthBoxed() {
        return delegate().getDayOfMonthBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ExplicitDateTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ExplicitDateDelegate delegate() {
        return (ExplicitDateDelegate)delegate;
    }

}