package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class FestivalsShortNameTranslatedTextsHollowFactory<T extends FestivalsShortNameTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new FestivalsShortNameTranslatedTextsHollow(((FestivalsShortNameTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new FestivalsShortNameTranslatedTextsHollow(new FestivalsShortNameTranslatedTextsDelegateCachedImpl((FestivalsShortNameTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}