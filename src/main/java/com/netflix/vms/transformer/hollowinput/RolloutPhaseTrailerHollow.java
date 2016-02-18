package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseTrailerHollow extends HollowObject {

    public RolloutPhaseTrailerHollow(RolloutPhaseTrailerDelegate delegate, int ordinal) {
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

    public RolloutPhasesElementsTrailerSupplementalInfoMapHollow _getSupplementalInfo() {
        int refOrdinal = delegate().getSupplementalInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getRolloutPhasesElementsTrailerSupplementalInfoMapHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseTrailerTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseTrailerDelegate delegate() {
        return (RolloutPhaseTrailerDelegate)delegate;
    }

}