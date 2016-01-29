package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CdnsDelegate extends HollowObjectDelegate {

    public int getNameOrdinal(int ordinal);

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public CdnsTypeAPI getTypeAPI();

}