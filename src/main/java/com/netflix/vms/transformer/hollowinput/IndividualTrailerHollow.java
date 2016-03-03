package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class IndividualTrailerHollow extends HollowObject {

    public IndividualTrailerHollow(IndividualTrailerDelegate delegate, int ordinal) {
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

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public IndividualTrailerTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected IndividualTrailerDelegate delegate() {
        return (IndividualTrailerDelegate)delegate;
    }

}