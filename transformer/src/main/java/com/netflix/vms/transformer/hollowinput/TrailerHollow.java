package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class TrailerHollow extends HollowObject {

    public TrailerHollow(TrailerDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public TrailersListHollow _getTrailers() {
        int refOrdinal = delegate().getTrailersOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTrailersListHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public TrailerTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TrailerDelegate delegate() {
        return (TrailerDelegate)delegate;
    }

}