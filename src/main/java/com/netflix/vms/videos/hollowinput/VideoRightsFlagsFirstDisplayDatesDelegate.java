package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoRightsFlagsFirstDisplayDatesDelegate extends HollowObjectDelegate {

    public long getValue(int ordinal);

    public Long getValueBoxed(int ordinal);

    public VideoRightsFlagsFirstDisplayDatesTypeAPI getTypeAPI();

}