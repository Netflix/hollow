package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface OriginServerDelegate extends HollowObjectDelegate {

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public int getNameOrdinal(int ordinal);

    public int getStorageGroupIdOrdinal(int ordinal);

    public OriginServerTypeAPI getTypeAPI();

}