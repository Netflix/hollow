package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class CacheDeploymentIntentHollow extends HollowObject {

    public CacheDeploymentIntentHollow(CacheDeploymentIntentDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getStreamProfileId() {
        return delegate().getStreamProfileId(ordinal);
    }

    public Long _getStreamProfileIdBoxed() {
        return delegate().getStreamProfileIdBoxed(ordinal);
    }

    public StringHollow _getIsoCountryCode() {
        int refOrdinal = delegate().getIsoCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getBitrateKBPS() {
        return delegate().getBitrateKBPS(ordinal);
    }

    public Long _getBitrateKBPSBoxed() {
        return delegate().getBitrateKBPSBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CacheDeploymentIntentTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CacheDeploymentIntentDelegate delegate() {
        return (CacheDeploymentIntentDelegate)delegate;
    }

}