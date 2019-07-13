package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutPhaseWindowDelegate extends HollowObjectDelegate {

    public long getEndDate(int ordinal);

    public Long getEndDateBoxed(int ordinal);

    public long getStartDate(int ordinal);

    public Long getStartDateBoxed(int ordinal);

    public RolloutPhaseWindowTypeAPI getTypeAPI();

}