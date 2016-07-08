package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoRightsWindowDelegate extends HollowObjectDelegate {

    public int getContractWindowStartDateOrdinal(int ordinal);

    public boolean getOnHold(int ordinal);

    public Boolean getOnHoldBoxed(int ordinal);

    public int getEndDateOrdinal(int ordinal);

    public int getLastUpdateTsOrdinal(int ordinal);

    public int getContractIdsOrdinal(int ordinal);

    public int getContractWindowEndDateOrdinal(int ordinal);

    public int getStartDateOrdinal(int ordinal);

    public VideoRightsWindowTypeAPI getTypeAPI();

}