package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface DamMerchStillsDelegate extends HollowObjectDelegate {

    public int getAssetIdOrdinal(int ordinal);

    public int getMomentOrdinal(int ordinal);

    public DamMerchStillsTypeAPI getTypeAPI();

}