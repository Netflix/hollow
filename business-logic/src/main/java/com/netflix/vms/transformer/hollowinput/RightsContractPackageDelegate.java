package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RightsContractPackageDelegate extends HollowObjectDelegate {

    public long getPackageId(int ordinal);

    public Long getPackageIdBoxed(int ordinal);

    public boolean getPrimary(int ordinal);

    public Boolean getPrimaryBoxed(int ordinal);

    public RightsContractPackageTypeAPI getTypeAPI();

}