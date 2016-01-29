package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface StorageGroupsDelegate extends HollowObjectDelegate {

    public long getCdnId(int ordinal);

    public Long getCdnIdBoxed(int ordinal);

    public int getIdOrdinal(int ordinal);

    public int getCountriesOrdinal(int ordinal);

    public StorageGroupsTypeAPI getTypeAPI();

}