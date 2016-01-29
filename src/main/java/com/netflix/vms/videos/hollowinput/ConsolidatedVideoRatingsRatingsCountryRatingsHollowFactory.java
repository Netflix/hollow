package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class ConsolidatedVideoRatingsRatingsCountryRatingsHollowFactory<T extends ConsolidatedVideoRatingsRatingsCountryRatingsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedVideoRatingsRatingsCountryRatingsHollow(((ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedVideoRatingsRatingsCountryRatingsHollow(new ConsolidatedVideoRatingsRatingsCountryRatingsDelegateCachedImpl((ConsolidatedVideoRatingsRatingsCountryRatingsTypeAPI)typeAPI, ordinal), ordinal);
    }

}