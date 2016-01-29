package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedCertificationSystemsRatingHollow extends HollowObject {

    public ConsolidatedCertificationSystemsRatingHollow(ConsolidatedCertificationSystemsRatingDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getRatingCode() {
        int refOrdinal = delegate().getRatingCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public ConsolidatedCertificationSystemsRatingRatingCodesHollow _getRatingCodes() {
        int refOrdinal = delegate().getRatingCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedCertificationSystemsRatingRatingCodesHollow(refOrdinal);
    }

    public long _getRatingId() {
        return delegate().getRatingId(ordinal);
    }

    public Long _getRatingIdBoxed() {
        return delegate().getRatingIdBoxed(ordinal);
    }

    public long _getMaturityLevel() {
        return delegate().getMaturityLevel(ordinal);
    }

    public Long _getMaturityLevelBoxed() {
        return delegate().getMaturityLevelBoxed(ordinal);
    }

    public ConsolidatedCertificationSystemsRatingDescriptionsHollow _getDescriptions() {
        int refOrdinal = delegate().getDescriptionsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedCertificationSystemsRatingDescriptionsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedCertificationSystemsRatingTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedCertificationSystemsRatingDelegate delegate() {
        return (ConsolidatedCertificationSystemsRatingDelegate)delegate;
    }

}