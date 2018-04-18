package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RightsContractPackageHollow extends HollowObject {

    public RightsContractPackageHollow(RightsContractPackageDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getPackageId() {
        return delegate().getPackageId(ordinal);
    }

    public Long _getPackageIdBoxed() {
        return delegate().getPackageIdBoxed(ordinal);
    }

    public boolean _getPrimary() {
        return delegate().getPrimary(ordinal);
    }

    public Boolean _getPrimaryBoxed() {
        return delegate().getPrimaryBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RightsContractPackageTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsContractPackageDelegate delegate() {
        return (RightsContractPackageDelegate)delegate;
    }

}