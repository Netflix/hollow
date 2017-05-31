package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RightsAssetsDelegate extends HollowObjectDelegate {

    public int getAssetSetIdOrdinal(int ordinal);

    public int getAssetsOrdinal(int ordinal);

    public RightsAssetsTypeAPI getTypeAPI();

}