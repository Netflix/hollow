package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ConsolidatedCertificationSystemsHollow extends HollowObject {

    public ConsolidatedCertificationSystemsHollow(ConsolidatedCertificationSystemsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getCertificationSystemId() {
        return delegate().getCertificationSystemId(ordinal);
    }

    public Long _getCertificationSystemIdBoxed() {
        return delegate().getCertificationSystemIdBoxed(ordinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public ConsolidatedCertSystemRatingListHollow _getRating() {
        int refOrdinal = delegate().getRatingOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedCertSystemRatingListHollow(refOrdinal);
    }

    public TranslatedTextHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getDescription() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public StringHollow _getOfficialURL() {
        int refOrdinal = delegate().getOfficialURLOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedCertificationSystemsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedCertificationSystemsDelegate delegate() {
        return (ConsolidatedCertificationSystemsDelegate)delegate;
    }

}