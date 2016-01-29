package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhasesWindowsDelegate extends HollowObjectDelegate {

    public long getEndDate(int ordinal);

    public Long getEndDateBoxed(int ordinal);

    public long getStartDate(int ordinal);

    public Long getStartDateBoxed(int ordinal);

    public RolloutPhasesWindowsTypeAPI getTypeAPI();

}