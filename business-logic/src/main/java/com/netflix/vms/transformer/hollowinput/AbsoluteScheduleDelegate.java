package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface AbsoluteScheduleDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public String getPhaseTag(int ordinal);

    public boolean isPhaseTagEqual(int ordinal, String testValue);

    public long getStartDate(int ordinal);

    public Long getStartDateBoxed(int ordinal);

    public long getEndDate(int ordinal);

    public Long getEndDateBoxed(int ordinal);

    public AbsoluteScheduleTypeAPI getTypeAPI();

}