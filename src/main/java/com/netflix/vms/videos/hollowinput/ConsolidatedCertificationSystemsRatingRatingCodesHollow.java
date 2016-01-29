package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedCertificationSystemsRatingRatingCodesHollow extends HollowObject {

    public ConsolidatedCertificationSystemsRatingRatingCodesHollow(ConsolidatedCertificationSystemsRatingRatingCodesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedCertificationSystemsRatingRatingCodesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedCertificationSystemsRatingRatingCodesDelegate delegate() {
        return (ConsolidatedCertificationSystemsRatingRatingCodesDelegate)delegate;
    }

}