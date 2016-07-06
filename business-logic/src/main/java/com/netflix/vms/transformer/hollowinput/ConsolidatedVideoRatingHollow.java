package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedVideoRatingHollow extends HollowObject {

    public ConsolidatedVideoRatingHollow(ConsolidatedVideoRatingDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ConsolidatedVideoCountryRatingListHollow _getCountryRatings() {
        int refOrdinal = delegate().getCountryRatingsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedVideoCountryRatingListHollow(refOrdinal);
    }

    public ISOCountryListHollow _getCountryList() {
        int refOrdinal = delegate().getCountryListOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getISOCountryListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedVideoRatingTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedVideoRatingDelegate delegate() {
        return (ConsolidatedVideoRatingDelegate)delegate;
    }

}