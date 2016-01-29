package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesElementsTrailersHollow extends HollowObject {

    public RolloutPhasesElementsTrailersHollow(RolloutPhasesElementsTrailersDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getSequenceNumber() {
        return delegate().getSequenceNumber(ordinal);
    }

    public Long _getSequenceNumberBoxed() {
        return delegate().getSequenceNumberBoxed(ordinal);
    }

    public long _getTrailerMovieId() {
        return delegate().getTrailerMovieId(ordinal);
    }

    public Long _getTrailerMovieIdBoxed() {
        return delegate().getTrailerMovieIdBoxed(ordinal);
    }

    public RolloutPhasesElementsTrailersMapOfSupplementalInfoHollow _getSupplementalInfo() {
        int refOrdinal = delegate().getSupplementalInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhasesElementsTrailersMapOfSupplementalInfoHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhasesElementsTrailersTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhasesElementsTrailersDelegate delegate() {
        return (RolloutPhasesElementsTrailersDelegate)delegate;
    }

}