package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RightsContractAssetDelegate extends HollowObjectDelegate {

    public String getBcp47Code(int ordinal);

    public boolean isBcp47CodeEqual(int ordinal, String testValue);

    public int getBcp47CodeOrdinal(int ordinal);

    public String getAssetType(int ordinal);

    public boolean isAssetTypeEqual(int ordinal, String testValue);

    public int getAssetTypeOrdinal(int ordinal);

    public RightsContractAssetTypeAPI getTypeAPI();

}