package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StreamAssetTypeDelegate extends HollowObjectDelegate {

    public long getAssetTypeId(int ordinal);

    public Long getAssetTypeIdBoxed(int ordinal);

    public int getAssetTypeOrdinal(int ordinal);

    public StreamAssetTypeTypeAPI getTypeAPI();

}