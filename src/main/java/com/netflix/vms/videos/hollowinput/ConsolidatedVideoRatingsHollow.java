package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedVideoRatingsHollow extends HollowObject {

    public ConsolidatedVideoRatingsHollow(ConsolidatedVideoRatingsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ConsolidatedVideoRatingsArrayOfRatingsHollow _getRatings() {
        int refOrdinal = delegate().getRatingsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedVideoRatingsArrayOfRatingsHollow(refOrdinal);
    }

    public long _getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long _getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedVideoRatingsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedVideoRatingsDelegate delegate() {
        return (ConsolidatedVideoRatingsDelegate)delegate;
    }

}