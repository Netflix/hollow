package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MasterScheduleDelegate extends HollowObjectDelegate {

    public int getMovieTypeOrdinal(int ordinal);

    public long getVersionId(int ordinal);

    public Long getVersionIdBoxed(int ordinal);

    public int getScheduleIdOrdinal(int ordinal);

    public int getPhaseTagOrdinal(int ordinal);

    public long getAvailabilityOffset(int ordinal);

    public Long getAvailabilityOffsetBoxed(int ordinal);

    public MasterScheduleTypeAPI getTypeAPI();

}