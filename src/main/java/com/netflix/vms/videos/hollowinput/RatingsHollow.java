package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RatingsHollow extends HollowObject {

    public RatingsHollow(RatingsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public RatingsRatingCodeHollow _getRatingCode() {
        int refOrdinal = delegate().getRatingCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRatingsRatingCodeHollow(refOrdinal);
    }

    public long _getRatingId() {
        return delegate().getRatingId(ordinal);
    }

    public Long _getRatingIdBoxed() {
        return delegate().getRatingIdBoxed(ordinal);
    }

    public RatingsDescriptionHollow _getDescription() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRatingsDescriptionHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RatingsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RatingsDelegate delegate() {
        return (RatingsDelegate)delegate;
    }

}