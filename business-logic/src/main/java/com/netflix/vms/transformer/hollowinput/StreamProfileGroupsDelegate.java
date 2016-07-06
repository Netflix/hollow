package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface StreamProfileGroupsDelegate extends HollowObjectDelegate {

    public int getGroupNameOrdinal(int ordinal);

    public int getStreamProfileIdsOrdinal(int ordinal);

    public StreamProfileGroupsTypeAPI getTypeAPI();

}