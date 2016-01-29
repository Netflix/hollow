package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoRightsRightsDelegate extends HollowObjectDelegate {

    public int getWindowsOrdinal(int ordinal);

    public int getContractsOrdinal(int ordinal);

    public VideoRightsRightsTypeAPI getTypeAPI();

}