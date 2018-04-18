package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RolloutPhaseWindowHollow extends HollowObject {

    public RolloutPhaseWindowHollow(RolloutPhaseWindowDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getEndDate() {
        return delegate().getEndDate(ordinal);
    }

    public Long _getEndDateBoxed() {
        return delegate().getEndDateBoxed(ordinal);
    }

    public long _getStartDate() {
        return delegate().getStartDate(ordinal);
    }

    public Long _getStartDateBoxed() {
        return delegate().getStartDateBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseWindowTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseWindowDelegate delegate() {
        return (RolloutPhaseWindowDelegate)delegate;
    }

}