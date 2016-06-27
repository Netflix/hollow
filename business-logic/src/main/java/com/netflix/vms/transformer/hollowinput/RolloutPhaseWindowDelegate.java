package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutPhaseWindowDelegate extends HollowObjectDelegate {

    public long getEndDate(int ordinal);

    public Long getEndDateBoxed(int ordinal);

    public long getStartDate(int ordinal);

    public Long getStartDateBoxed(int ordinal);

    public RolloutPhaseWindowTypeAPI getTypeAPI();

}