package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowMapCachedDelegate;

public class FestivalsFestivalNameMapOfTranslatedTextsHollowFactory<T extends FestivalsFestivalNameMapOfTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new FestivalsFestivalNameMapOfTranslatedTextsHollow(((FestivalsFestivalNameMapOfTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new FestivalsFestivalNameMapOfTranslatedTextsHollow(new HollowMapCachedDelegate((FestivalsFestivalNameMapOfTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}