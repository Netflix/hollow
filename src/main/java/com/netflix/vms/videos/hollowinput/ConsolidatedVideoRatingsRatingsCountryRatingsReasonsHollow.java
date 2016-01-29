package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow extends HollowObject {

    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsHollow(ConsolidatedVideoRatingsRatingsCountryRatingsReasonsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedVideoRatingsRatingsCountryRatingsReasonsMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsReasonsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedVideoRatingsRatingsCountryRatingsReasonsDelegate delegate() {
        return (ConsolidatedVideoRatingsRatingsCountryRatingsReasonsDelegate)delegate;
    }

}