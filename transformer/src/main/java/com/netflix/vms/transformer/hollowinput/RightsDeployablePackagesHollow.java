package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RightsDeployablePackagesHollow extends HollowObject {

    public RightsDeployablePackagesHollow(RightsDeployablePackagesDelegate delegate, int ordinal) {
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

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RightsDeployablePackagesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsDeployablePackagesDelegate delegate() {
        return (RightsDeployablePackagesDelegate)delegate;
    }

}