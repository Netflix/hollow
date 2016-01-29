package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class RatingsRatingCodeTranslatedTextsHollowFactory<T extends RatingsRatingCodeTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RatingsRatingCodeTranslatedTextsHollow(((RatingsRatingCodeTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RatingsRatingCodeTranslatedTextsHollow(new RatingsRatingCodeTranslatedTextsDelegateCachedImpl((RatingsRatingCodeTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}