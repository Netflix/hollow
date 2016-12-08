package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MasterScheduleDelegate extends HollowObjectDelegate {

    public String getMovieType(int ordinal);

    public boolean isMovieTypeEqual(int ordinal, String testValue);

    public long getVersionId(int ordinal);

    public Long getVersionIdBoxed(int ordinal);

    public String getScheduleId(int ordinal);

    public boolean isScheduleIdEqual(int ordinal, String testValue);

    public String getPhaseTag(int ordinal);

    public boolean isPhaseTagEqual(int ordinal, String testValue);

    public long getAvailabilityOffset(int ordinal);

    public Long getAvailabilityOffsetBoxed(int ordinal);

    public MasterScheduleTypeAPI getTypeAPI();

}