package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoRightsRightsWindowsDelegate extends HollowObjectDelegate {

    public long getContractWindowStartDate(int ordinal);

    public Long getContractWindowStartDateBoxed(int ordinal);

    public boolean getOnHold(int ordinal);

    public Boolean getOnHoldBoxed(int ordinal);

    public long getEndDate(int ordinal);

    public Long getEndDateBoxed(int ordinal);

    public long getLastUpdateTs(int ordinal);

    public Long getLastUpdateTsBoxed(int ordinal);

    public int getContractIdsOrdinal(int ordinal);

    public long getContractWindowEndDate(int ordinal);

    public Long getContractWindowEndDateBoxed(int ordinal);

    public long getStartDate(int ordinal);

    public Long getStartDateBoxed(int ordinal);

    public VideoRightsRightsWindowsTypeAPI getTypeAPI();

}