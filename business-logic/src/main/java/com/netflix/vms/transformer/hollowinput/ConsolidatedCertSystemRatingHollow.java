package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class ConsolidatedCertSystemRatingHollow extends HollowObject {

    public ConsolidatedCertSystemRatingHollow(ConsolidatedCertSystemRatingDelegate delegate, int ordinal) {
        super(delegate, ordinal);
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

    public StringHollow _getRatingCode() {
        int refOrdinal = delegate().getRatingCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public TranslatedTextHollow _getRatingCodes() {
        int refOrdinal = delegate().getRatingCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getDescriptions() {
        int refOrdinal = delegate().getDescriptionsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedCertSystemRatingTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedCertSystemRatingDelegate delegate() {
        return (ConsolidatedCertSystemRatingDelegate)delegate;
    }

}