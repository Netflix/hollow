package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class CertificationSystemHollow extends HollowObject {

    public CertificationSystemHollow(CertificationSystemDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getCertificationSystemId() {
        return delegate().getCertificationSystemId(ordinal);
    }

    public Long _getCertificationSystemIdBoxed() {
        return delegate().getCertificationSystemIdBoxed(ordinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public CertificationSystemRatingListHollow _getRating() {
        int refOrdinal = delegate().getRatingOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCertificationSystemRatingListHollow(refOrdinal);
    }

    public StringHollow _getOfficialURL() {
        int refOrdinal = delegate().getOfficialURLOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CertificationSystemTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CertificationSystemDelegate delegate() {
        return (CertificationSystemDelegate)delegate;
    }

}