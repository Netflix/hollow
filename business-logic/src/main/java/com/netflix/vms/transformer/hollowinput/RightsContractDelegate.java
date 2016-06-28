package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RightsContractDelegate extends HollowObjectDelegate {

    public int getAssetsOrdinal(int ordinal);

    public long getContractId(int ordinal);

    public Long getContractIdBoxed(int ordinal);

    public long getPackageId(int ordinal);

    public Long getPackageIdBoxed(int ordinal);

    public int getPackagesOrdinal(int ordinal);

    public RightsContractTypeAPI getTypeAPI();

}