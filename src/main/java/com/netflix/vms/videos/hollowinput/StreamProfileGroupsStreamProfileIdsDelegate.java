package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface StreamProfileGroupsStreamProfileIdsDelegate extends HollowObjectDelegate {

    public long getValue(int ordinal);

    public Long getValueBoxed(int ordinal);

    public StreamProfileGroupsStreamProfileIdsTypeAPI getTypeAPI();

}