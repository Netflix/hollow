package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RightsWindowContract extends HollowObject {

    public RightsWindowContract(RightsWindowContractDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ListOfRightsContractAsset getAssets() {
        int refOrdinal = delegate().getAssetsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfRightsContractAsset(refOrdinal);
    }

    public long getDealId() {
        return delegate().getDealId(ordinal);
    }

    public Long getDealIdBoxed() {
        return delegate().getDealIdBoxed(ordinal);
    }

    public long getPackageId() {
        return delegate().getPackageId(ordinal);
    }

    public Long getPackageIdBoxed() {
        return delegate().getPackageIdBoxed(ordinal);
    }

    public ListOfRightsContractPackage getPackages() {
        int refOrdinal = delegate().getPackagesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfRightsContractPackage(refOrdinal);
    }

    public boolean getDownload() {
        return delegate().getDownload(ordinal);
    }

    public Boolean getDownloadBoxed() {
        return delegate().getDownloadBoxed(ordinal);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public RightsWindowContractTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsWindowContractDelegate delegate() {
        return (RightsWindowContractDelegate)delegate;
    }

}