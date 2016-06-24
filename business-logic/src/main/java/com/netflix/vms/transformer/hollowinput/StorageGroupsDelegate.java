package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StorageGroupsDelegate extends HollowObjectDelegate {

    public int getIdOrdinal(int ordinal);

    public long getCdnId(int ordinal);

    public Long getCdnIdBoxed(int ordinal);

    public int getCountriesOrdinal(int ordinal);

    public StorageGroupsTypeAPI getTypeAPI();

}