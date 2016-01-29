package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class AwardsDescriptionTranslatedTextsHollowFactory<T extends AwardsDescriptionTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new AwardsDescriptionTranslatedTextsHollow(((AwardsDescriptionTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new AwardsDescriptionTranslatedTextsHollow(new AwardsDescriptionTranslatedTextsDelegateCachedImpl((AwardsDescriptionTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}