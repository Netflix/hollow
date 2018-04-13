package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class IndividualSupplementalHollow extends HollowObject {

    public IndividualSupplementalHollow(IndividualSupplementalDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getIdentifier() {
        int refOrdinal = delegate().getIdentifierOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public PassthroughDataHollow _getPassthrough() {
        int refOrdinal = delegate().getPassthroughOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPassthroughDataHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public IndividualSupplementalTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected IndividualSupplementalDelegate delegate() {
        return (IndividualSupplementalDelegate)delegate;
    }

}