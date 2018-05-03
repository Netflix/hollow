package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class IPLDerivativeGroupHollow extends HollowObject {

    public IPLDerivativeGroupHollow(IPLDerivativeGroupDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getExternalId() {
        int refOrdinal = delegate().getExternalIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public int _getSubmission() {
        return delegate().getSubmission(ordinal);
    }

    public Integer _getSubmissionBoxed() {
        return delegate().getSubmissionBoxed(ordinal);
    }

    public StringHollow _getImageType() {
        int refOrdinal = delegate().getImageTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public IPLDerivativeSetHollow _getDerivatives() {
        int refOrdinal = delegate().getDerivativesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getIPLDerivativeSetHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public IPLDerivativeGroupTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected IPLDerivativeGroupDelegate delegate() {
        return (IPLDerivativeGroupDelegate)delegate;
    }

}