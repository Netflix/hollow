package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow extends HollowObject {

    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesHollow(ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public boolean _getOrdered() {
        return delegate().getOrdered(ordinal);
    }

    public Boolean _getOrderedBoxed() {
        return delegate().getOrderedBoxed(ordinal);
    }

    public boolean _getImageOnly() {
        return delegate().getImageOnly(ordinal);
    }

    public Boolean _getImageOnlyBoxed() {
        return delegate().getImageOnlyBoxed(ordinal);
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow _getIds() {
        int refOrdinal = delegate().getIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesArrayOfIdsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesDelegate delegate() {
        return (ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesDelegate)delegate;
    }

}