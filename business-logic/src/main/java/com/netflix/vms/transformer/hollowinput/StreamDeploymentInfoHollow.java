package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
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