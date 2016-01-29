package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedVideoRatingsRatingsCountryRatingsHollow extends HollowObject {

    public ConsolidatedVideoRatingsRatingsCountryRatingsHollow(ConsolidatedVideoRatingsRatingsCountryRatingsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow _getAdvisories() {
        int refOrdinal = delegate().getAdvisoriesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow(refOrdinal);
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow _getReasons() {
        int refOrdinal = delegate().getReasonsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow(refOrdinal);
    }

    public long _getRatingId() {
        return delegate().getRatingId(ordinal);
    }

    public Long _getRatingIdBoxed() {
        return delegate().getRatingIdBoxed(ordinal);
    }

    public long _getCertificationSystemId() {
        return delegate().getCertificationSystemId(ordinal);
    }

    public Long _getCertificationSystemIdBoxed() {
        return delegate().getCertificationSystemIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedVideoRatingsRatingsCountryRatingsDelegate delegate() {
        return (ConsolidatedVideoRatingsRatingsCountryRatingsDelegate)delegate;
    }

}