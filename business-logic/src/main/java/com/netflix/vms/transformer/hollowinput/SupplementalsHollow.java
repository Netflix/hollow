package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class SupplementalsHollow extends HollowObject {

    public SupplementalsHollow(SupplementalsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public SupplementalsListHollow _getSupplementals() {
        int refOrdinal = delegate().getSupplementalsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSupplementalsListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public SupplementalsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected SupplementalsDelegate delegate() {
        return (SupplementalsDelegate)delegate;
    }

}