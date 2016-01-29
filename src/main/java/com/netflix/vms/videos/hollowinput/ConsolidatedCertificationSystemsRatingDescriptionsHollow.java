package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedCertificationSystemsRatingDescriptionsHollow extends HollowObject {

    public ConsolidatedCertificationSystemsRatingDescriptionsHollow(ConsolidatedCertificationSystemsRatingDescriptionsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedCertificationSystemsRatingDescriptionsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedCertificationSystemsRatingDescriptionsDelegate delegate() {
        return (ConsolidatedCertificationSystemsRatingDescriptionsDelegate)delegate;
    }

}