package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ExplicitDateHollow extends HollowObject {

    public ExplicitDateHollow(ExplicitDateDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMonthOfYear() {
        return delegate().getMonthOfYear(ordinal);
    }

    public Long _getMonthOfYearBoxed() {
        return delegate().getMonthOfYearBoxed(ordinal);
    }

    public long _getYear() {
        return delegate().getYear(ordinal);
    }

    public Long _getYearBoxed() {
        return delegate().getYearBoxed(ordinal);
    }

    public long _getDayOfMonth() {
        return delegate().getDayOfMonth(ordinal);
    }

    public Long _getDayOfMonthBoxed() {
        return delegate().getDayOfMonthBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ExplicitDateTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ExplicitDateDelegate delegate() {
        return (ExplicitDateDelegate)delegate;
    }

}