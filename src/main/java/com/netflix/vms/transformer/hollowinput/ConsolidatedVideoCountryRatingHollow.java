package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedVideoCountryRatingHollow extends HollowObject {

    public ConsolidatedVideoCountryRatingHollow(ConsolidatedVideoCountryRatingDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public VideoRatingAdvisoriesHollow _getAdvisories() {
        int refOrdinal = delegate().getAdvisoriesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRatingAdvisoriesHollow(refOrdinal);
    }

    public TranslatedTextHollow _getReasons() {
        int refOrdinal = delegate().getReasonsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
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

    public ConsolidatedVideoCountryRatingTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedVideoCountryRatingDelegate delegate() {
        return (ConsolidatedVideoCountryRatingDelegate)delegate;
    }

}