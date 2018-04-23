package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class DeployablePackagesHollow extends HollowObject {

    public DeployablePackagesHollow(DeployablePackagesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
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

    public ISOCountrySetHollow _getCountryCodes() {
        int refOrdinal = delegate().getCountryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getISOCountrySetHollow(refOrdinal);
    }

    public ListOfPackageTagsHollow _getTags() {
        int refOrdinal = delegate().getTagsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfPackageTagsHollow(refOrdinal);
    }

    public boolean _getDefaultPackage() {
        return delegate().getDefaultPackage(ordinal);
    }

    public Boolean _getDefaultPackageBoxed() {
        return delegate().getDefaultPackageBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DeployablePackagesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DeployablePackagesDelegate delegate() {
        return (DeployablePackagesDelegate)delegate;
    }

}