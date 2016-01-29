package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedVideoRatingsRatingsHollow extends HollowObject {

    public ConsolidatedVideoRatingsRatingsHollow(ConsolidatedVideoRatingsRatingsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow _getCountryRatings() {
        int refOrdinal = delegate().getCountryRatingsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedVideoRatingsRatingsArrayOfCountryRatingsHollow(refOrdinal);
    }

    public ConsolidatedVideoRatingsRatingsArrayOfCountryListHollow _getCountryList() {
        int refOrdinal = delegate().getCountryListOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedVideoRatingsRatingsArrayOfCountryListHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedVideoRatingsRatingsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedVideoRatingsRatingsDelegate delegate() {
        return (ConsolidatedVideoRatingsRatingsDelegate)delegate;
    }

}