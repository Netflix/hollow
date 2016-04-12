package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhaseWindowDelegate extends HollowObjectDelegate {

    public int getEndDateOrdinal(int ordinal);

    public int getStartDateOrdinal(int ordinal);

    public RolloutPhaseWindowTypeAPI getTypeAPI();

}