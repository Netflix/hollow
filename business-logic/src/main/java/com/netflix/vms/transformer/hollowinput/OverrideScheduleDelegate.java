package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface OverrideScheduleDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getPhaseTagOrdinal(int ordinal);

    public long getAvailabilityOffset(int ordinal);

    public Long getAvailabilityOffsetBoxed(int ordinal);

    public OverrideScheduleTypeAPI getTypeAPI();

}