package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface OriginServersDelegate extends HollowObjectDelegate {

    public int getStorageGroupIdOrdinal(int ordinal);

    public int getNameOrdinal(int ordinal);

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public OriginServersTypeAPI getTypeAPI();

}