package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RightsContractAssetDelegate extends HollowObjectDelegate {

    public int getBcp47CodeOrdinal(int ordinal);

    public int getAssetTypeOrdinal(int ordinal);

    public RightsContractAssetTypeAPI getTypeAPI();

}