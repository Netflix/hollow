package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowListCachedDelegate;

public class ConsolidatedVideoRatingsRatingsArrayOfCountryListHollowFactory<T extends ConsolidatedVideoRatingsRatingsArrayOfCountryListHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedVideoRatingsRatingsArrayOfCountryListHollow(((ConsolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedVideoRatingsRatingsArrayOfCountryListHollow(new HollowListCachedDelegate((ConsolidatedVideoRatingsRatingsArrayOfCountryListTypeAPI)typeAPI, ordinal), ordinal);
    }

}