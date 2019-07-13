package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RolloutPhaseWindow extends HollowObject {

    public RolloutPhaseWindow(RolloutPhaseWindowDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getEndDate() {
        return delegate().getEndDate(ordinal);
    }

    public Long getEndDateBoxed() {
        return delegate().getEndDateBoxed(ordinal);
    }

    public long getStartDate() {
        return delegate().getStartDate(ordinal);
    }

    public Long getStartDateBoxed() {
        return delegate().getStartDateBoxed(ordinal);
    }

    public RolloutAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseWindowTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseWindowDelegate delegate() {
        return (RolloutPhaseWindowDelegate)delegate;
    }

}