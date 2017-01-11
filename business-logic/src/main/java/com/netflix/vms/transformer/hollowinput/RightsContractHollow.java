package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RightsContractHollow extends HollowObject {

    public RightsContractHollow(RightsContractDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ListOfRightsContractAssetHollow _getAssets() {
        int refOrdinal = delegate().getAssetsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfRightsContractAssetHollow(refOrdinal);
    }

    public long _getContractId() {
        return delegate().getContractId(ordinal);
    }

    public Long _getContractIdBoxed() {
        return delegate().getContractIdBoxed(ordinal);
    }

    public long _getPackageId() {
        return delegate().getPackageId(ordinal);
    }

    public Long _getPackageIdBoxed() {
        return delegate().getPackageIdBoxed(ordinal);
    }

    public ListOfRightsContractPackageHollow _getPackages() {
        int refOrdinal = delegate().getPackagesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfRightsContractPackageHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RightsContractTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsContractDelegate delegate() {
        return (RightsContractDelegate)delegate;
    }

}