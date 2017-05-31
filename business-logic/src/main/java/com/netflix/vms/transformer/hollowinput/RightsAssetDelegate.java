package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RightsAssetDelegate extends HollowObjectDelegate {

    public int getBcp47CodeOrdinal(int ordinal);

    public int getAssetTypeOrdinal(int ordinal);

    public RightsAssetTypeAPI getTypeAPI();

}