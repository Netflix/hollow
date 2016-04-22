package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class StreamDeploymentInfoHollow extends HollowObject {

    public StreamDeploymentInfoHollow(StreamDeploymentInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ISOCountrySetHollow _getCacheDeployedCountries() {
        int refOrdinal = delegate().getCacheDeployedCountriesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getISOCountrySetHollow(refOrdinal);
    }

    public CdnDeploymentSetHollow _getCdnDeployments() {
        int refOrdinal = delegate().getCdnDeploymentsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCdnDeploymentSetHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamDeploymentInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected StreamDeploymentInfoDelegate delegate() {
        return (StreamDeploymentInfoDelegate)delegate;
    }

}