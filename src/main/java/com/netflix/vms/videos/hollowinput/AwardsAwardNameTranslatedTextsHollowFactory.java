package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class AwardsAwardNameTranslatedTextsHollowFactory<T extends AwardsAwardNameTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new AwardsAwardNameTranslatedTextsHollow(((AwardsAwardNameTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new AwardsAwardNameTranslatedTextsHollow(new AwardsAwardNameTranslatedTextsDelegateCachedImpl((AwardsAwardNameTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}