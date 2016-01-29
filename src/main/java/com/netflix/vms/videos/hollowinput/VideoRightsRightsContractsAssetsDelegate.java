package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoRightsRightsContractsAssetsDelegate extends HollowObjectDelegate {

    public int getBcp47CodeOrdinal(int ordinal);

    public int getAssetTypeOrdinal(int ordinal);

    public VideoRightsRightsContractsAssetsTypeAPI getTypeAPI();

}