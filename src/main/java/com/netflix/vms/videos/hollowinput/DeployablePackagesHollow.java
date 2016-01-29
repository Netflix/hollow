package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class DeployablePackagesHollow extends HollowObject {

    public DeployablePackagesHollow(DeployablePackagesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public DeployablePackagesArrayOfCountryCodesHollow _getCountryCodes() {
        int refOrdinal = delegate().getCountryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDeployablePackagesArrayOfCountryCodesHollow(refOrdinal);
    }

    public long _getPackageId() {
        return delegate().getPackageId(ordinal);
    }

    public Long _getPackageIdBoxed() {
        return delegate().getPackageIdBoxed(ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public DeployablePackagesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DeployablePackagesDelegate delegate() {
        return (DeployablePackagesDelegate)delegate;
    }

}