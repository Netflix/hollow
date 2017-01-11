package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface DamMerchStillsMomentDelegate extends HollowObjectDelegate {

    public int getPackageIdOrdinal(int ordinal);

    public int getStillTSOrdinal(int ordinal);

    public DamMerchStillsMomentTypeAPI getTypeAPI();

}