package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

@SuppressWarnings("all")
public class ConsolidatedVideoRatingsHollowFactory<T extends ConsolidatedVideoRatingsHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedVideoRatingsHollow(((ConsolidatedVideoRatingsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedVideoRatingsHollow(new ConsolidatedVideoRatingsDelegateCachedImpl((ConsolidatedVideoRatingsTypeAPI)typeAPI, ordinal), ordinal);
    }

}