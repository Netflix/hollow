package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface PhaseTagDelegate extends HollowObjectDelegate {

    public int getPhaseTagOrdinal(int ordinal);

    public int getScheduleIdOrdinal(int ordinal);

    public PhaseTagTypeAPI getTypeAPI();

}